package com.camoutech.mapper;

import com.camoutech.exception.SubscriptionException;
import com.camoutech.modal.Subscription;
import com.camoutech.modal.SubscriptionPlan;
import com.camoutech.modal.User;
import com.camoutech.payload.dto.SubscriptionDTO;
import com.camoutech.repository.SubscriptionPlanRepository;
import com.camoutech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 03/01/2026
 */

@RequiredArgsConstructor
@Component
public class SubscriptionMapper {

    private final UserRepository userRepository;
    private final SubscriptionPlanRepository planRepository;

    /**
     * Convert Subscription entity to DTO
     */
    public SubscriptionDTO toDTO(Subscription subscription) {
        if (subscription == null) {
            return null;
        }

        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setId(subscription.getId());

        // User information
        if (subscription.getUser() != null) {
            dto.setUserId(subscription.getUser().getId());
            dto.setUserName(subscription.getUser().getFullName());
            dto.setUserEmail(subscription.getUser().getEmail());

        }

        //Plan information
        if (subscription.getPlan() != null) {
            dto.setPlanId(subscription.getPlan().getId());
        }
        dto.setPlanName(subscription.getPlanName());
        dto.setPlanCode(subscription.getPlanCode());
        dto.setPrice(subscription.getPrice());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setIsActive(subscription.getIsActive());
        dto.setMaxBooksAllowed(subscription.getMaxBooksAllowed());
        dto.setMaxDaysPerBook(subscription.getMaxDaysPerBook());
        dto.setAutoRenew(subscription.getAutoRenew());
        dto.setCancelledAt(subscription.getCancelledAt());
        dto.setCancellationReason(subscription.getCancellationReason());
        dto.setNotes(subscription.getNotes());

        // Calculated fields
        dto.setDaysRemaining(subscription.getDaysRemaining());
        dto.setIsValid(subscription.isValid());
        dto.setIsExpired(subscription.isExpired());

        return dto;
    }

    /**
     * Convert DTO to Subscription entity
     */
    public Subscription toEntity(SubscriptionDTO dto) throws SubscriptionException {
        if (dto == null) {
            return null;
        }

        Subscription subscription = new Subscription();
        subscription.setId(dto.getId());

        // Map user
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(()-> new SubscriptionException("User not found with ID: " + dto.getUserId()));
            subscription.setUser(user);
        }

        // Map plan
        if (dto.getPlanId() != null) {
            SubscriptionPlan plan = planRepository.findById(dto.getPlanId())
                    .orElseThrow(()-> new SubscriptionException("Subscription plan not found whith ID" + dto.getPlanId()));
            subscription.setPlan(plan);
        }

        subscription.setPlanName(dto.getPlanName());
        subscription.setPlanCode(dto.getPlanCode());
        subscription.setPrice(dto.getPrice());

        subscription.setStartDate(dto.getStartDate());
        subscription.setEndDate(dto.getEndDate());
        subscription.setIsActive(dto.getIsActive());
        subscription.setMaxBooksAllowed(dto.getMaxBooksAllowed());
        subscription.setMaxDaysPerBook(dto.getMaxDaysPerBook());
        subscription.setAutoRenew(dto.getAutoRenew() != null ? dto.getAutoRenew() : false);
        subscription.setCancelledAt(dto.getCancelledAt());
        subscription.setCancellationReason(dto.getCancellationReason());
        subscription.setNotes(dto.getNotes());

        return subscription;
    }

    /**
     * Convert list of subscriptions to DTOS
     */
    public List<SubscriptionDTO> toDTOList(List<Subscription> subscriptions) {
        if (subscriptions == null) {
            return null;
        }
        return subscriptions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
