package com.hilary.leedtech.controller;

import com.hilary.leedtech.dto.FeePaymentRequest;
import com.hilary.leedtech.dto.FeePaymentResponse;
import com.hilary.leedtech.services.FeePaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/one-time-fee-payment")
@RequiredArgsConstructor
@Slf4j
public class FeePaymentController {

    private final FeePaymentService feePaymentService;

    @PostMapping
    public ResponseEntity<FeePaymentResponse> processPayment(@Valid @RequestBody FeePaymentRequest request) {
        log.info("Processing payment for student: {}", request.getStudentNumber());
        return ResponseEntity.ok(feePaymentService.processPayment(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<FeePaymentResponse>> getPaymentHistory(@RequestParam String studentNumber) {
        log.info("Fetching payment history for student: {}", studentNumber);
        return ResponseEntity.ok(feePaymentService.getPaymentHistory(studentNumber));
    }
}
