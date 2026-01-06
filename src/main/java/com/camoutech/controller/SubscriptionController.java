package com.camoutech.controller;

import com.camoutech.exception.SubscriptionException;
import com.camoutech.payload.dto.SubscriptionDTO;
import com.camoutech.payload.response.ApiResponse;
import com.camoutech.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody SubscriptionDTO subscriptionDTO) throws Exception {
        SubscriptionDTO dto = subscriptionService.subscribe(subscriptionDTO);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/active")
    public ResponseEntity<?> getUsersSubscriptions(@RequestParam(required = false) Long userId) throws Exception {
        SubscriptionDTO dto = subscriptionService.getUsersActiveSubscription(userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAllSubscriptions() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page,size);
        List<SubscriptionDTO> dtoList = subscriptionService.getAllSubscriptions(pageable);
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/admin/deactivate-expired")
    public ResponseEntity<?> deactivateExpiredSubscriptions() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page,size);
        subscriptionService.deactivateExpiredSubscriptions();
        ApiResponse res = new ApiResponse("task done!", true);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/cancel/{subscriptionId}")
    public ResponseEntity<?> cancelSubscription(
            @PathVariable Long subscriptionId,
            @RequestParam(required = false) String reason) throws SubscriptionException {
        SubscriptionDTO subscriptionDTO = subscriptionService.cancelSubscription(subscriptionId, reason);

        return ResponseEntity.ok(subscriptionDTO);
    }

    @PostMapping("/active")
    public ResponseEntity<?> activeSubscription(@RequestParam Long subscriptionId, @RequestParam Long paymentId) throws SubscriptionException {
        SubscriptionDTO subscriptionDTO = subscriptionService.activatedSubscription(subscriptionId, paymentId);
        return ResponseEntity.ok(subscriptionDTO);
    }
}
