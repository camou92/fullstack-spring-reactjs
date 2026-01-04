package com.camoutech.service;

import com.camoutech.payload.dto.SubscriptionDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 03/01/2026
 */
public interface SubscriptionService {

    SubscriptionDTO subscribe(SubscriptionDTO subscriptionDTO);

    SubscriptionDTO getUsersActiveSubscription(Long userId);

    SubscriptionDTO cancelSubscription(Long subscriptionId, String reason);

    SubscriptionDTO activeSubscription(Long subscriptionId, Long paymentId);

    List<SubscriptionDTO> getAllSubscriptions(Pageable pageable);
}
