package com.camoutech.service.gateway;

import com.camoutech.domain.PaymentType;
import com.camoutech.modal.Payment;
import com.camoutech.modal.SubscriptionPlan;
import com.camoutech.modal.User;
import com.camoutech.payload.response.PaymentLinkResponse;
import com.camoutech.service.SubscriptionPlanService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class StripeService {

    private final SubscriptionPlanService subscriptionPlanService;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    /**
     * Création du lien de paiement Stripe (équivalent Razorpay PaymentLink)
     */
    public PaymentLinkResponse createPaymentLink(User user, Payment payment) {

        try {
            Stripe.apiKey = stripeSecretKey;

            // ===== Amount en centimes (Stripe l'exige)
            Long amountInCents = BigDecimal
                    .valueOf(payment.getAmount())
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP)
                    .longValueExact();

            SessionCreateParams.Builder params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setCustomerEmail(user.getEmail())
                            .setSuccessUrl(
                                    successUrl.replace(
                                            "{PAYMENT_ID}",
                                            payment.getId().toString()
                                    ) + "?session_id={CHECKOUT_SESSION_ID}"
                            )
                            .setCancelUrl(cancelUrl)
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.PriceData.builder()
                                                            .setCurrency("usd") // ou "mad"
                                                            .setUnitAmount(amountInCents)
                                                            .setProductData(
                                                                    SessionCreateParams.ProductData.builder()
                                                                            .setName(payment.getDescription())
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            // ===== METADATA (équivalent notes Razorpay)
                            .putMetadata("user_id", user.getId().toString())
                            .putMetadata("payment_id", payment.getId().toString())
                            .putMetadata("type", payment.getPaymentType().name());

            if (payment.getPaymentType() == PaymentType.MEMBERSHIP) {
                params.putMetadata(
                        "subscription_id",
                        payment.getSubscription().getId().toString()
                );
                params.putMetadata(
                        "plan",
                        payment.getSubscription().getPlan().getPlanCode()
                );
            }

            Session session = Session.create(params.build());

            PaymentLinkResponse response = new PaymentLinkResponse();
            response.setPayment_link_id(session.getId());
            response.setPayment_link_url(session.getUrl());
            return response;

        } catch (StripeException e) {
            throw new RuntimeException("Stripe payment creation failed", e);
        }
    }

    /**
     * Validation du paiement (simple, hors webhook)
     */
    public boolean isValidPayment(String sessionId) {

        try {
            Stripe.apiKey = stripeSecretKey;

            Session session = Session.retrieve(sessionId);

            if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
                return false;
            }

            Long amountPaid = session.getAmountTotal() != null
                    ? session.getAmountTotal() / 100
                    : 0L;

            String paymentType = session.getMetadata().get("type");

            if (PaymentType.MEMBERSHIP.name().equals(paymentType)) {
                String planCode = session.getMetadata().get("plan");
                SubscriptionPlan plan =
                        subscriptionPlanService.getBySubscriptionPlanCode(planCode);

                return amountPaid.equals(plan.getPrice());
            }

            if (PaymentType.FINE.name().equals(paymentType)) {
                return true;
            }

            return false;

        } catch (Exception e) {
            throw new RuntimeException("Stripe payment validation failed", e);
        }
    }
}
