package com.example.webhook.repo;

import com.example.webhook.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByFirstNameAndLastName(String firstName, String lastName);
}
