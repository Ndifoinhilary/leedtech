package com.hilary.leedtech.repository;

import com.hilary.leedtech.model.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {

    List<FeePayment> findByStudentAccountStudentNumber(String studentNumber);
}
