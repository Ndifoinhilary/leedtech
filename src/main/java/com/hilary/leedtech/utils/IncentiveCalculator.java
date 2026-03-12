package com.hilary.leedtech.utils;

import java.math.BigDecimal;

public class IncentiveCalculator {

    private static final BigDecimal TIER_ONE_LIMIT = new BigDecimal("100000");
    private static final BigDecimal TIER_TWO_LIMIT = new BigDecimal("500000");
    private static final BigDecimal RATE_ONE = new BigDecimal("0.01");
    private static final BigDecimal RATE_TWO = new BigDecimal("0.03");
    private static final BigDecimal RATE_THREE = new BigDecimal("0.05");

    private IncentiveCalculator() {
    }

    public static BigDecimal incentiveRate(BigDecimal paymentAmount) {

        if (paymentAmount.compareTo(TIER_ONE_LIMIT) < 0) {
            return RATE_ONE;
        }

        if (paymentAmount.compareTo(TIER_TWO_LIMIT) < 0) {
            return RATE_TWO;
        }

        return RATE_THREE;
    }

    public static BigDecimal incentiveAmount(BigDecimal paymentAmount) {

        BigDecimal rate = incentiveRate(paymentAmount);

        return paymentAmount.multiply(rate);
    }
}
