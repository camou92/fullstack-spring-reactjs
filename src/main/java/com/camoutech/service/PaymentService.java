package com.camoutech.service;

import com.camoutech.payload.dto.PaymentDTO;
import com.camoutech.payload.request.PaymentInitiateRequest;
import com.camoutech.payload.request.PaymentVerifyRequest;
import com.camoutech.payload.response.PaymentInitiateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    PaymentInitiateResponse initiatePayment(PaymentInitiateRequest req) throws Exception;

    PaymentDTO verifyPayment(PaymentVerifyRequest request) throws Exception;

    Page<PaymentDTO> getAllPayments(Pageable pageable);
}
