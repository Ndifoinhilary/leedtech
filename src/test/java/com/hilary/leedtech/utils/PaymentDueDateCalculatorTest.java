package com.hilary.leedtech.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentDueDateCalculatorTest {

    private static final long FREQUENCY = 90;

    @Test
    @DisplayName("Doc Example 1: March 14 2026 + 90 days = June 12 2026 (Friday, no adjustment)")
    void docExample1_weekday_noAdjustment() {
        LocalDate paymentDate = LocalDate.of(2026, 3, 14);
        LocalDate result = PaymentDueDateCalculator.calculateNextPaymentDueDate(paymentDate, FREQUENCY);

        assertThat(result).isEqualTo(LocalDate.of(2026, 6, 12));
        assertThat(result.getDayOfWeek()).isEqualTo(DayOfWeek.FRIDAY);
    }

    @Test
    @DisplayName("Doc Example 2: April 5 2026 + 90 days = July 4 (Saturday) → adjusted to Monday July 6")
    void docExample2_saturday_adjustedToMonday() {
        LocalDate paymentDate = LocalDate.of(2026, 4, 5);
        LocalDate result = PaymentDueDateCalculator.calculateNextPaymentDueDate(paymentDate, FREQUENCY);

        assertThat(result).isEqualTo(LocalDate.of(2026, 7, 6));
        assertThat(result.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    }

    @Test
    @DisplayName("Due date falls on Sunday → adjusted to Monday")
    void sundayDueDate_adjustedToMonday() {

        LocalDate paymentDate = LocalDate.of(2026, 1, 5);
        LocalDate raw = paymentDate.plusDays(FREQUENCY);
        assertThat(raw.getDayOfWeek()).isEqualTo(DayOfWeek.SUNDAY);

        LocalDate result = PaymentDueDateCalculator.calculateNextPaymentDueDate(paymentDate, FREQUENCY);
        assertThat(result).isEqualTo(LocalDate.of(2026, 4, 6)); // Monday
        assertThat(result.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    }

    @Test
    @DisplayName("Due date falls on Saturday → adjusted to Monday (+2 days)")
    void saturdayDueDate_adjustedToMonday() {
        LocalDate paymentDate = LocalDate.of(2026, 1, 4);
        LocalDate raw = paymentDate.plusDays(FREQUENCY);
        assertThat(raw.getDayOfWeek()).isEqualTo(DayOfWeek.SATURDAY);

        LocalDate result = PaymentDueDateCalculator.calculateNextPaymentDueDate(paymentDate, FREQUENCY);
        assertThat(result).isEqualTo(LocalDate.of(2026, 4, 6)); // Monday
        assertThat(result.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    }

    @ParameterizedTest(name = "Payment on {0} → due date {1} (weekday)")
    @CsvSource({
            "2026-01-01, 2026-04-01",
            "2026-01-02, 2026-04-02",
            "2026-01-06, 2026-04-06",
            "2026-03-14, 2026-06-12",
    })
    void weekdayDueDates_noAdjustment(String paymentStr, String expectedStr) {
        LocalDate payment = LocalDate.parse(paymentStr);
        LocalDate expected = LocalDate.parse(expectedStr);
        LocalDate result = PaymentDueDateCalculator.calculateNextPaymentDueDate(payment, FREQUENCY);

        assertThat(result).isEqualTo(expected);
        assertThat(result.getDayOfWeek()).isNotIn(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    }

    @Test
    @DisplayName("Result should never be Saturday or Sunday")
    void resultNeverOnWeekend() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        for (int i = 0; i < 365; i++) {
            LocalDate date = start.plusDays(i);
            LocalDate result = PaymentDueDateCalculator.calculateNextPaymentDueDate(date, FREQUENCY);
            assertThat(result.getDayOfWeek())
                    .as("Due date for payment on %s should be a weekday, but was %s", date, result.getDayOfWeek())
                    .isNotIn(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        }
    }
}

