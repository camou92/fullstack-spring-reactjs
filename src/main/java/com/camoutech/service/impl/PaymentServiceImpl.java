package com.camoutech.service.impl;

import com.camoutech.domain.PaymentGateway;
import com.camoutech.domain.PaymentStatus;
import com.camoutech.event.publisher.PaymentEventPublisher;
import com.camoutech.mapper.PaymentMapper;
import com.camoutech.modal.Payment;
import com.camoutech.modal.Subscription;
import com.camoutech.modal.User;
import com.camoutech.payload.dto.PaymentDTO;
import com.camoutech.payload.request.PaymentInitiateRequest;
import com.camoutech.payload.request.PaymentVerifyRequest;
import com.camoutech.payload.response.PaymentInitiateResponse;
import com.camoutech.payload.response.PaymentLinkResponse;
import com.camoutech.repository.PaymentRepository;
import com.camoutech.repository.SubscriptionRepository;
import com.camoutech.repository.UserRepository;
import com.camoutech.service.PaymentService;
import com.camoutech.service.gateway.RazorpayService;
import com.camoutech.service.gateway.StripeService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final StripeService razorpayService;
    private final PaymentMapper paymentMapper;
    private final PaymentEventPublisher paymentEventPublisher;

    @Override
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) throws Exception {

        User user = userRepository.findById(request.getUserId()).get();

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPaymentType(request.getPaymentType());
        payment.setGateway(request.getGateway());
        payment.setAmount(request.getAmount());

        payment.setDescription(request.getDescription());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId("TXN_" + UUID.randomUUID());
        payment.setInitiatedAt(LocalDateTime.now());

        if (request.getSubscriptionId() != null) {
            Subscription sub = subscriptionRepository
                    .findById(request.getSubscriptionId())
                    .orElseThrow(() -> new Exception("Subscription not found"));
        }
        payment = paymentRepository.save(payment);

        PaymentInitiateResponse response = new PaymentInitiateResponse();
        if (request.getGateway() == PaymentGateway.RAZORPAY) {
            PaymentLinkResponse paymentLinkResponse = razorpayService.createPaymentLink(user, payment);
            response = PaymentInitiateResponse.builder()
                    .paymentId(payment.getId())
                    .gateway(payment.getGateway())
                    .checkoutUrl(paymentLinkResponse.getPayment_link_url())
                    .transactionId(paymentLinkResponse.getPayment_link_id())
                    .amount(payment.getAmount())
                    .description(payment.getDescription())
                    .success(true)
                    .message("Payment initiated successfully")
                    .build();

            payment.setGatewayOrderId(paymentLinkResponse.getPayment_link_id());
        }
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);
        return response;
    }

    @Override
    public PaymentDTO verifyPayment(PaymentVerifyRequest request) throws Exception {
        JSONObject paymentDetails = razorpayService.fetchPaymentDetails(
                request.getRazorpayPaymentId()
        );

        JSONObject notes = paymentDetails.getJSONObject("notes");

        Long paymentId = Long.parseLong(notes.optString("payment_id"));

        Payment payment = paymentRepository.findById(paymentId).get();

        boolean isValid = razorpayService.isValidPayment(request.getRazorpayPaymentId());

        if (PaymentGateway.RAZORPAY == payment.getGateway()) {
            if (isValid) {
                payment.setGatewayOrderId(request.getRazorpayPaymentId());
            }
        }

        if (isValid) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setCompletedAt(LocalDateTime.now());
            payment = paymentRepository.save(payment);

            // publish payment success event
            paymentEventPublisher.publishPaymentSuccessEvent(payment);
        }
        return paymentMapper.toDTO(payment);
    }

    @Override
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAll(pageable);

        return payments.map(paymentMapper::toDTO);
    }
}
