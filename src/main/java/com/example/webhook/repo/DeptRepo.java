package com.example.webhook.repo;

import com.example.webhook.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeptRepo extends JpaRepository<Department, Long> {
    Department findByDepartmentName(String departmentName);
}