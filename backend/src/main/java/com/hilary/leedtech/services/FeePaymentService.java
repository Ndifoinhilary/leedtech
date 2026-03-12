package com.hilary.leedtech.services;

import com.hilary.leedtech.dto.FeePaymentRequest;
import com.hilary.leedtech.dto.FeePaymentResponse;

import java.util.List;

public interface FeePaymentService {

    FeePaymentResponse processPayment(FeePaymentRequest request);


    List<FeePaymentResponse> getPaymentHistory(String studentNumber);
}
