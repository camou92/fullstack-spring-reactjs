package com.camoutech.controller;

import com.camoutech.payload.dto.SubscriptionPlanDTO;
import com.camoutech.payload.response.ApiResponse;
import com.camoutech.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 02/01/2026
 */

@RequestMapping("/api/subscription-plans")
@RestController
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    @GetMapping
    public ResponseEntity<?> getAllSubscriptionPlans() {
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getAllSubscriptionPlan();
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/admin/create")
    public ResponseEntity<?> createSubscriptionPlan(@Valid @RequestBody SubscriptionPlanDTO subscriptionPlanDTO) throws Exception {
        SubscriptionPlanDTO plans = subscriptionPlanService.createSubscriptionPlan(subscriptionPlanDTO);
        return ResponseEntity.ok(plans);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateSubscriptionPlan(
            @Valid @RequestBody SubscriptionPlanDTO subscriptionPlanDTO,
            @PathVariable long id) throws Exception {
        SubscriptionPlanDTO plans = subscriptionPlanService
                .updateSubscriptionPlan(id,subscriptionPlanDTO);
        return ResponseEntity.ok(plans);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteSubscriptionPlan(
            @PathVariable long id) throws Exception {
        subscriptionPlanService.deleteSubscription(id);
        ApiResponse res = new ApiResponse("plan deleted successfully", true);
        return ResponseEntity.ok(res);
    }
}
