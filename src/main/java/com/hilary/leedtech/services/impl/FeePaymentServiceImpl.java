package com.hilary.leedtech.services.impl;

import com.hilary.leedtech.dto.FeePaymentRequest;
import com.hilary.leedtech.dto.FeePaymentResponse;
import com.hilary.leedtech.exception.ResourceNotFoundException;
import com.hilary.leedtech.model.FeePayment;
import com.hilary.leedtech.model.StudentAccount;
import com.hilary.leedtech.repository.FeePaymentRepository;
import com.hilary.leedtech.repository.StudentAccountRepository;
import com.hilary.leedtech.services.FeePaymentService;
import com.hilary.leedtech.utils.IncentiveCalculator;
import com.hilary.leedtech.utils.PaymentDueDateCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeePaymentServiceImpl implements FeePaymentService {

    private static final long PAYMENT_FREQUENCY_DAYS = 90;
    private final FeePaymentRepository feePaymentRepository;
    private final StudentAccountRepository studentAccountRepository;

    @Override
    @Transactional
    public FeePaymentResponse processPayment(FeePaymentRequest request) {
        log.info("Processing payment for student: {}", request.getStudentNumber());

        StudentAccount studentAccount = studentAccountRepository.findByStudentNumber(request.getStudentNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student account not found for student number: " + request.getStudentNumber()));

        LocalDate paymentDate = request.getPaymentDate() != null ? request.getPaymentDate() : LocalDate.now();

        BigDecimal incentiveRate = IncentiveCalculator.incentiveRate(request.getPaymentAmount());
        BigDecimal incentiveAmount = IncentiveCalculator.incentiveAmount(request.getPaymentAmount());

        BigDecimal previousBalance = studentAccount.getBalance();

        BigDecimal totalReduction = request.getPaymentAmount().add(incentiveAmount);
        BigDecimal newBalance = previousBalance.subtract(totalReduction);

        LocalDate nextDueDate = PaymentDueDateCalculator.calculateNextPaymentDueDate(paymentDate, PAYMENT_FREQUENCY_DAYS);

        studentAccount.setBalance(newBalance);
        studentAccount.setNextPaymentDueDate(nextDueDate);
        studentAccountRepository.save(studentAccount);

        FeePayment feePayment = FeePayment.builder()
                .paymentDate(paymentDate)
                .paymentAmount(request.getPaymentAmount())
                .incentiveRate(incentiveRate)
                .incentiveAmount(incentiveAmount)
                .studentAccount(studentAccount)
                .build();
        feePaymentRepository.save(feePayment);

        return FeePaymentResponse.builder()
                .studentNumber(studentAccount.getStudentNumber())
                .previousBalance(previousBalance)
                .paymentAmount(request.getPaymentAmount())
                .incentiveRate(incentiveRate)
                .incentiveAmount(incentiveAmount)
                .newBalance(newBalance)
                .nextPaymentDueDate(nextDueDate)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeePaymentResponse> getPaymentHistory(String studentNumber) {
        log.info("Fetching payment history for student: {}", studentNumber);

        StudentAccount studentAccount = studentAccountRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student account not found for student number: " + studentNumber));

        List<FeePayment> feePayments = feePaymentRepository.findByStudentAccountStudentNumber(studentNumber);

        return feePayments.stream()
                .map(fp -> FeePaymentResponse.builder()
                        .studentNumber(studentAccount.getStudentNumber())
                        .paymentAmount(fp.getPaymentAmount())
                        .incentiveRate(fp.getIncentiveRate())
                        .incentiveAmount(fp.getIncentiveAmount())
                        .nextPaymentDueDate(studentAccount.getNextPaymentDueDate())
                        .build())
                .toList();
    }
}
