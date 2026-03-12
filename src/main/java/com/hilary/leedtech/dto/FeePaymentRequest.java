package com.hilary.leedtech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FeePaymentRequest {
    @NotBlank(message = "Student ID is required")
    private String studentId;

    private LocalDate paymentDate;

    @Positive(message = "Payment amount must be positive")
    private BigDecimal paymentAmount;
}
