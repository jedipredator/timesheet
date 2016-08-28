package com.confinale.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Employee.
 */
@Entity
@Table(name = "employee")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "hourly_rate", nullable = false)
    private Double hourlyRate;

    @OneToOne
    @JoinColumn(unique = true)
    private User employeeLogin;

    @ManyToOne
    private User headOfEmployee;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "employee_subsidiary",
               joinColumns = @JoinColumn(name="employees_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="subsidiaries_id", referencedColumnName="ID"))
    private Set<User> subsidiaries = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Employee name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public Employee hourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
        return this;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public User getEmployeeLogin() {
        return employeeLogin;
    }

    public Employee employeeLogin(User user) {
        this.employeeLogin = user;
        return this;
    }

    public void setEmployeeLogin(User user) {
        this.employeeLogin = user;
    }

    public User getHeadOfEmployee() {
        return headOfEmployee;
    }

    public Employee headOfEmployee(User user) {
        this.headOfEmployee = user;
        return this;
    }

    public void setHeadOfEmployee(User user) {
        this.headOfEmployee = user;
    }

    public Set<User> getSubsidiaries() {
        return subsidiaries;
    }

    public Employee subsidiaries(Set<User> users) {
        this.subsidiaries = users;
        return this;
    }

    public Employee addUser(User user) {
        subsidiaries.add(user);
        return this;
    }

    public Employee removeUser(User user) {
        subsidiaries.remove(user);
        return this;
    }

    public void setSubsidiaries(Set<User> users) {
        this.subsidiaries = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Employee employee = (Employee) o;
        if(employee.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Employee{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", hourlyRate='" + hourlyRate + "'" +
            '}';
    }
}
