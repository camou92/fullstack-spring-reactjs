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
import com.camoutech.service.gateway.StripeService;
import lombok.RequiredArgsConstructor;
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
    private final StripeService stripeService;
    private final PaymentMapper paymentMapper;
    private final PaymentEventPublisher paymentEventPublisher;

    // =========================
    // INITIATE PAYMENT (Stripe)
    // =========================
    @Override
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) throws Exception {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new Exception("User not found"));

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPaymentType(request.getPaymentType());
        payment.setGateway(PaymentGateway.STRIPE);
        payment.setAmount(request.getAmount());
        payment.setDescription(request.getDescription());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId("TXN_" + UUID.randomUUID());
        payment.setInitiatedAt(LocalDateTime.now());

        if (request.getSubscriptionId() != null) {
            Subscription subscription = subscriptionRepository
                    .findById(request.getSubscriptionId())
                    .orElseThrow(() -> new Exception("Subscription not found"));
            payment.setSubscription(subscription);
        }

        payment = paymentRepository.save(payment);

        // ===== Création du lien de paiement Stripe (Price dynamique)
        PaymentLinkResponse paymentLinkResponse = stripeService.createPaymentLink(user, payment);

        payment.setGatewayOrderId(paymentLinkResponse.getPayment_link_id());
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);

        return PaymentInitiateResponse.builder()
                .paymentId(payment.getId())
                .gateway(payment.getGateway())
                .checkoutUrl(paymentLinkResponse.getPayment_link_url())
                .transactionId(paymentLinkResponse.getPayment_link_id())
                .amount(payment.getAmount())
                .description(payment.getDescription())
                .success(true)
                .message("Stripe payment initiated successfully")
                .build();
    }

    // =========================
    // VERIFY PAYMENT (Stripe)
    // =========================
    @Override
    public PaymentDTO verifyPayment(PaymentVerifyRequest request) throws Exception {

        String sessionId = request.getSessionId();

        Payment payment = paymentRepository
                .findByGatewayOrderId(sessionId)
                .orElseThrow(() -> new Exception("Payment not found"));

        boolean isValid = stripeService.isValidPayment(sessionId);

        if (isValid) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setCompletedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            // 🔔 Event paiement réussi
            paymentEventPublisher.publishPaymentSuccessEvent(payment);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }

        return paymentMapper.toDTO(payment);
    }

    // =========================
    // GET ALL PAYMENTS
    // =========================
    @Override
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(paymentMapper::toDTO);
    }
}
