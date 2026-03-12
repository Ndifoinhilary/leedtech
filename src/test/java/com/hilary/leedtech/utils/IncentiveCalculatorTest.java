package com.hilary.leedtech.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class IncentiveCalculatorTest {


    @Nested
    @DisplayName("Incentive rate tier selection")
    class IncentiveRateTests {

        @Test
        @DisplayName("Amount just above zero (e.g. 0.01) → 1% tier")
        void smallestPositiveAmount_shouldReturnTierOneRate() {
            assertThat(IncentiveCalculator.incentiveRate(new BigDecimal("0.01")))
                    .isEqualByComparingTo(new BigDecimal("0.01"));
        }

        @Test
        @DisplayName("Amount 99,999.99 (just below 100K) → 1% tier")
        void justBelowTierTwoThreshold_shouldReturnTierOneRate() {
            assertThat(IncentiveCalculator.incentiveRate(new BigDecimal("99999.99")))
                    .isEqualByComparingTo(new BigDecimal("0.01"));
        }

        @Test
        @DisplayName("Amount exactly 100,000 → 3% tier")
        void exactlyAtTierTwoThreshold_shouldReturnTierTwoRate() {
            assertThat(IncentiveCalculator.incentiveRate(new BigDecimal("100000")))
                    .isEqualByComparingTo(new BigDecimal("0.03"));
        }

        @Test
        @DisplayName("Amount 100,000.01 → 3% tier")
        void justAboveTierTwoThreshold_shouldReturnTierTwoRate() {
            assertThat(IncentiveCalculator.incentiveRate(new BigDecimal("100000.01")))
                    .isEqualByComparingTo(new BigDecimal("0.03"));
        }

        @Test
        @DisplayName("Amount 499,999.99 (just below 500K) → 3% tier")
        void justBelowTierThreeThreshold_shouldReturnTierTwoRate() {
            assertThat(IncentiveCalculator.incentiveRate(new BigDecimal("499999.99")))
                    .isEqualByComparingTo(new BigDecimal("0.03"));
        }

        @Test
        @DisplayName("Amount exactly 500,000 → 5% tier")
        void exactlyAtTierThreeThreshold_shouldReturnTierThreeRate() {
            assertThat(IncentiveCalculator.incentiveRate(new BigDecimal("500000")))
                    .isEqualByComparingTo(new BigDecimal("0.05"));
        }

        @Test
        @DisplayName("Amount 500,000.01 → 5% tier")
        void justAboveTierThreeThreshold_shouldReturnTierThreeRate() {
            assertThat(IncentiveCalculator.incentiveRate(new BigDecimal("500000.01")))
                    .isEqualByComparingTo(new BigDecimal("0.05"));
        }

        @Test
        @DisplayName("Very large amount (1,000,000) → 5% tier")
        void veryLargeAmount_shouldReturnTierThreeRate() {
            assertThat(IncentiveCalculator.incentiveRate(new BigDecimal("1000000")))
                    .isEqualByComparingTo(new BigDecimal("0.05"));
        }
    }


    @Nested
    @DisplayName("Incentive amount calculation")
    class IncentiveAmountTests {

        @ParameterizedTest(name = "payment={0} → incentive={1}")
        @CsvSource({
                "50000,   500.00",
                "99999.99, 999.9999",
                "100000,  3000.00",
                "200000,  6000.00",
                "499999.99, 14999.9997",
                "500000,  25000.00",
                "575000,  28750.00",
        })
        void shouldCalculateCorrectIncentiveAmount(String payment, String expectedIncentive) {
            BigDecimal result = IncentiveCalculator.incentiveAmount(new BigDecimal(payment));
            assertThat(result).isEqualByComparingTo(new BigDecimal(expectedIncentive));
        }

        @Test
        @DisplayName("Doc Example 1: 100K payment → incentive = 3,000")
        void docExample1() {
            BigDecimal incentive = IncentiveCalculator.incentiveAmount(new BigDecimal("100000"));
            assertThat(incentive).isEqualByComparingTo(new BigDecimal("3000"));
        }

        @Test
        @DisplayName("Doc Example 2: 575K payment → incentive = 28,750")
        void docExample2() {
            BigDecimal incentive = IncentiveCalculator.incentiveAmount(new BigDecimal("575000"));
            assertThat(incentive).isEqualByComparingTo(new BigDecimal("28750"));
        }
    }
}

