package com.hilary.leedtech.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FeePaymentResponse {
    private String studentNumber;
    private BigDecimal previousBalance;
    private BigDecimal paymentAmount;
    private Double incentiveRate;
    private BigDecimal incentiveAmount;
    private BigDecimal newBalance;
    private LocalDate nextPaymentDueDate;
}
