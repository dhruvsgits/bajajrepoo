package com.example.webhook.repo;

import com.example.webhook.entity.Payments;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payments, Long> {
    List<Payments> findByEmployeeEmpId(Long empId);
}
