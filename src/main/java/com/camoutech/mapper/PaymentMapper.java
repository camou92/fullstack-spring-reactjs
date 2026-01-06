package com.camoutech.mapper;

import com.camoutech.modal.Payment;
import com.camoutech.payload.dto.PaymentDTO;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    /* =======================
     * Entity -> DTO
     * ======================= */
    public PaymentDTO toDTO(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setUserId(payment.getUser().getId());
        dto.setUserName(payment.getUser().getFullName());
        dto.setUserEmail(payment.getUser().getEmail());
        dto.setSubscriptionId(payment.getSubscription().getId());
        dto.setPaymentType(payment.getPaymentType());
        dto.setStatus(payment.getStatus());
        dto.setGateway(payment.getGateway());
        dto.setAmount(payment.getAmount());
        dto.setTransactionId(payment.getTransactionId());
        dto.setGatewayPaymentId(payment.getGatewayPaymentId());
        dto.setGatewayOrderId(payment.getGatewayOrderId());
        dto.setGatewaySignature(payment.getGatewaySignature());
        dto.setDescription(payment.getDescription());
        dto.setFailureReason(payment.getFailureReason());
        dto.setInitiatedAt(payment.getInitiatedAt());
        dto.setCompletedAt(payment.getCompletedAt());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());

        return dto;
    }

    /* =======================
     * DTO -> Entity
     * ======================= */
    /*public Payment toEntity(PaymentDTO dto) {
        if (dto == null) {
            return null;
        }

        Payment payment = new Payment();
        payment.setId(dto.getId());
        payment.setUserId(dto.getUserId());
        payment.setUserName(dto.getUserName());
        payment.setUserEmail(dto.getUserEmail());
        payment.setBookLoanId(dto.getBookLoanId());
        payment.setSubscriptionId(dto.getSubscriptionId());
        payment.setPaymentType(dto.getPaymentType());
        payment.setStatus(dto.getStatus());
        payment.setGateway(dto.getGateway());
        payment.setAmount(dto.getAmount());
        payment.setTransactionId(dto.getTransactionId());
        payment.setGatewayPaymentId(dto.getGatewayPaymentId());
        payment.setGatewayOrderId(dto.getGatewayOrderId());
        payment.setGatewaySignature(dto.getGatewaySignature());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setDescription(dto.getDescription());
        payment.setFailureReason(dto.getFailureReason());
        payment.setRetryCount(dto.getRetryCount());
        payment.setInitiatedAt(dto.getInitiatedAt());
        payment.setCompletedAt(dto.getCompletedAt());
        payment.setCreatedAt(dto.getCreatedAt());
        payment.setUpdatedAt(dto.getUpdatedAt());

        return payment;
    }*/
}

