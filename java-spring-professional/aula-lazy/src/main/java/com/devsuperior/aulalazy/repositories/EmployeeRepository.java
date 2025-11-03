package com.devsuperior.aulalazy.repositories;

import com.devsuperior.aulalazy.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT obj FROM Employee obj JOIN FETCH obj.department")
    List<Employee> findEmployeesWithDepartments();

    List<Employee> findByNameContainingIgnoreCase(String name);
}
