package com.confinale.repository;

import com.confinale.domain.Employee;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Employee entity.
 */
@SuppressWarnings("unused")
public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    @Query("select employee from Employee employee where employee.headOfEmployee.login = ?#{principal.username}")
    List<Employee> findByHeadOfEmployeeIsCurrentUser();

    @Query("select distinct employee from Employee employee left join fetch employee.subsidiaries")
    List<Employee> findAllWithEagerRelationships();

    @Query("select employee from Employee employee left join fetch employee.subsidiaries where employee.id =:id")
    Employee findOneWithEagerRelationships(@Param("id") Long id);

}
