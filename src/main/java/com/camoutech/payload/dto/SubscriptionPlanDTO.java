package com.camoutech.payload.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 02/01/2026
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPlanDTO {

    private Long id;

    @NotBlank(message = "Plan code is mandatory")
    private String planCode;

    @NotBlank(message = "name is mandatory")
    private String name;

    private String description;

    @NotNull(message = "Duration is mandatory")
    @Positive(message = "Duration must be positive")
    private Integer durationDays;

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be positive")
    private Long price;

    private String currency;

    @NotNull(message = "Max books allowed is mandatory")
    @Positive(message = "Max books must be positive")
    private Integer maxBooksAllowed;

    @NotNull(message = "Max days per book is mandatory")
    @Positive(message = "Max days must be positive")
    private Integer maxDaysPerBook;

    private Integer displayOrder;
    private Boolean isActive;
    private Boolean isFeatured;
    private String badgeText;
    private String adminNotes;
    private String createdBy;
    private String updatedBy;

}
