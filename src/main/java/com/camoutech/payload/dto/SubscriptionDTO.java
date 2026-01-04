package com.camoutech.payload.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 03/01/2026
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {

    private Long id;

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    private String userName;
    private String userEmail;

    @NotNull(message = "Subscription plan ID is mandatory")
    private Long planId;

    private String planName;
    private String planCode;
    private Long price;
    private String currency;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private Integer maxBooksAllowed;
    private Integer maxDaysPerBook;
    private Boolean autoRenew;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private String notes;
    private Long daysRemaining;
    private Boolean isValid;
    private Boolean isExpired;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
