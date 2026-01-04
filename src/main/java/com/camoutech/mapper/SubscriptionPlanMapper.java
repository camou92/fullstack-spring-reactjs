package com.camoutech.mapper;

import com.camoutech.modal.SubscriptionPlan;
import com.camoutech.payload.dto.SubscriptionPlanDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 02/01/2026
 */

@Component
public class SubscriptionPlanMapper {

    public SubscriptionPlanDTO toDTO(SubscriptionPlan plan) {
        if (plan == null) {
            return null;
        }

        SubscriptionPlanDTO dto = new SubscriptionPlanDTO();
        dto.setId(plan.getId());
        dto.setPlanCode(plan.getPlanCode());
        dto.setName(plan.getName());
        dto.setDescription(plan.getDescription());
        dto.setDurationDays(plan.getDurationDays());
        dto.setPrice(plan.getPrice());
        dto.setMaxBooksAllowed(plan.getMaxBooksAllowed());
        dto.setMaxDaysPerBook(plan.getMaxDaysPerBook());
        dto.setDisplayOrder(plan.getDisplayOrder());
        dto.setIsActive(plan.getIsActive());
        dto.setIsFeatured(plan.getIsFeatured());
        dto.setBadgeText(plan.getBadgeText());
        dto.setAdminNotes(plan.getAdminNotes());

        return dto;
    }

    public SubscriptionPlan toEntity(SubscriptionPlanDTO dto) {
        if (dto == null) {
            return null;
        }

        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setId(dto.getId());
        plan.setPlanCode(dto.getPlanCode());
        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setDurationDays(dto.getDurationDays());
        plan.setPrice(dto.getPrice());
        plan.setMaxBooksAllowed(dto.getMaxBooksAllowed());
        plan.setMaxDaysPerBook(dto.getMaxDaysPerBook());
        plan.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        plan.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        plan.setIsFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false);
        plan.setBadgeText(dto.getBadgeText());
        plan.setAdminNotes(dto.getAdminNotes());
        plan.setCreatedBy(dto.getCreatedBy());
        plan.setUpdatedBy(dto.getUpdatedBy());

        return plan;
    }

    public void updateEntityFromDTO(SubscriptionPlan entity, SubscriptionPlanDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDurationDays(dto.getDurationDays());
        entity.setPrice(dto.getPrice());
        entity.setCurrency(dto.getCurrency());
        entity.setMaxBooksAllowed(dto.getMaxBooksAllowed());
        entity.setMaxDaysPerBook(dto.getMaxDaysPerBook());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setIsActive(dto.getIsActive());
        entity.setIsFeatured(dto.getIsFeatured());
        entity.setBadgeText(dto.getBadgeText());
        entity.setAdminNotes(dto.getAdminNotes());

    }



}
