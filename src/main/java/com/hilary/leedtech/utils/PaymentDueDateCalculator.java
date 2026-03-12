package com.hilary.leedtech.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class PaymentDueDateCalculator {

    private PaymentDueDateCalculator() {
    }

    public static LocalDate calculateNextPaymentDueDate(LocalDate paymentDate, long paymentFrequency) {
        LocalDate nextPaymentDueDate = paymentDate.plusDays(paymentFrequency);

        if (nextPaymentDueDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return nextPaymentDueDate.plusDays(2);
        }

        if (nextPaymentDueDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return nextPaymentDueDate.plusDays(1);
        }

        return nextPaymentDueDate;

    }
}
