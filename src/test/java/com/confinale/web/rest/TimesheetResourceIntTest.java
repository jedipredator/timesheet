package com.confinale.web.rest;

import com.confinale.TimeSheetApp;
import com.confinale.domain.Timesheet;
import com.confinale.domain.Project;
import com.confinale.domain.Employee;
import com.confinale.repository.TimesheetRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TimesheetResource REST controller.
 *
 * @see TimesheetResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TimeSheetApp.class)
public class TimesheetResourceIntTest {

    private static final Boolean DEFAULT_APPROVED = false;
    private static final Boolean UPDATED_APPROVED = true;

    private static final Integer DEFAULT_HOURS = 0;
    private static final Integer UPDATED_HOURS = 1;

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private TimesheetRepository timesheetRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restTimesheetMockMvc;

    private Timesheet timesheet;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TimesheetResource timesheetResource = new TimesheetResource();
        ReflectionTestUtils.setField(timesheetResource, "timesheetRepository", timesheetRepository);
        this.restTimesheetMockMvc = MockMvcBuilders.standaloneSetup(timesheetResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Timesheet createEntity(EntityManager em) {
        Timesheet timesheet = new Timesheet();
        timesheet = new Timesheet()
                .approved(DEFAULT_APPROVED)
                .hours(DEFAULT_HOURS)
                .date(DEFAULT_DATE);
        // Add required entity
        Project project = ProjectResourceIntTest.createEntity(em);
        em.persist(project);
        em.flush();
        timesheet.setProject(project);
        // Add required entity
        Employee employee = EmployeeResourceIntTest.createEntity(em);
        em.persist(employee);
        em.flush();
        timesheet.setEmployee(employee);
        return timesheet;
    }

    @Before
    public void initTest() {
        timesheet = createEntity(em);
    }

    @Test
    @Transactional
    public void createTimesheet() throws Exception {
        int databaseSizeBeforeCreate = timesheetRepository.findAll().size();

        // Create the Timesheet

        restTimesheetMockMvc.perform(post("/api/timesheets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timesheet)))
                .andExpect(status().isCreated());

        // Validate the Timesheet in the database
        List<Timesheet> timesheets = timesheetRepository.findAll();
        assertThat(timesheets).hasSize(databaseSizeBeforeCreate + 1);
        Timesheet testTimesheet = timesheets.get(timesheets.size() - 1);
        assertThat(testTimesheet.isApproved()).isEqualTo(DEFAULT_APPROVED);
        assertThat(testTimesheet.getHours()).isEqualTo(DEFAULT_HOURS);
        assertThat(testTimesheet.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = timesheetRepository.findAll().size();
        // set the field null
        timesheet.setDate(null);

        // Create the Timesheet, which fails.

        restTimesheetMockMvc.perform(post("/api/timesheets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timesheet)))
                .andExpect(status().isBadRequest());

        List<Timesheet> timesheets = timesheetRepository.findAll();
        assertThat(timesheets).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTimesheets() throws Exception {
        // Initialize the database
        timesheetRepository.saveAndFlush(timesheet);

        // Get all the timesheets
        restTimesheetMockMvc.perform(get("/api/timesheets?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(timesheet.getId().intValue())))
                .andExpect(jsonPath("$.[*].approved").value(hasItem(DEFAULT_APPROVED.booleanValue())))
                .andExpect(jsonPath("$.[*].hours").value(hasItem(DEFAULT_HOURS)))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    public void getTimesheet() throws Exception {
        // Initialize the database
        timesheetRepository.saveAndFlush(timesheet);

        // Get the timesheet
        restTimesheetMockMvc.perform(get("/api/timesheets/{id}", timesheet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(timesheet.getId().intValue()))
            .andExpect(jsonPath("$.approved").value(DEFAULT_APPROVED.booleanValue()))
            .andExpect(jsonPath("$.hours").value(DEFAULT_HOURS))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTimesheet() throws Exception {
        // Get the timesheet
        restTimesheetMockMvc.perform(get("/api/timesheets/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTimesheet() throws Exception {
        // Initialize the database
        timesheetRepository.saveAndFlush(timesheet);
        int databaseSizeBeforeUpdate = timesheetRepository.findAll().size();

        // Update the timesheet
        Timesheet updatedTimesheet = timesheetRepository.findOne(timesheet.getId());
        updatedTimesheet
                .approved(UPDATED_APPROVED)
                .hours(UPDATED_HOURS)
                .date(UPDATED_DATE);

        restTimesheetMockMvc.perform(put("/api/timesheets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTimesheet)))
                .andExpect(status().isOk());

        // Validate the Timesheet in the database
        List<Timesheet> timesheets = timesheetRepository.findAll();
        assertThat(timesheets).hasSize(databaseSizeBeforeUpdate);
        Timesheet testTimesheet = timesheets.get(timesheets.size() - 1);
        assertThat(testTimesheet.isApproved()).isEqualTo(UPDATED_APPROVED);
        assertThat(testTimesheet.getHours()).isEqualTo(UPDATED_HOURS);
        assertThat(testTimesheet.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    public void deleteTimesheet() throws Exception {
        // Initialize the database
        timesheetRepository.saveAndFlush(timesheet);
        int databaseSizeBeforeDelete = timesheetRepository.findAll().size();

        // Get the timesheet
        restTimesheetMockMvc.perform(delete("/api/timesheets/{id}", timesheet.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Timesheet> timesheets = timesheetRepository.findAll();
        assertThat(timesheets).hasSize(databaseSizeBeforeDelete - 1);
    }
}
