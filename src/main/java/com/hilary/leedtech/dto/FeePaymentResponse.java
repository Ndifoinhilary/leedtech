package com.hilary.leedtech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeePaymentResponse {
    private String studentNumber;
    private BigDecimal previousBalance;
    private BigDecimal paymentAmount;
    private BigDecimal incentiveRate;
    private BigDecimal incentiveAmount;
    private BigDecimal newBalance;
    private LocalDate nextPaymentDueDate;
}
