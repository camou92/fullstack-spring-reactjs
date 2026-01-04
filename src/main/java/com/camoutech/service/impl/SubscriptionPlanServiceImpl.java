package com.camoutech.service.impl;

import com.camoutech.mapper.SubscriptionPlanMapper;
import com.camoutech.modal.SubscriptionPlan;
import com.camoutech.modal.User;
import com.camoutech.payload.dto.SubscriptionPlanDTO;
import com.camoutech.repository.SubscriptionPlanRepository;
import com.camoutech.service.SubscriptionPlanService;
import com.camoutech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Auteur : Mohamed Camara
 * Email : cmohamed992@gmail.com
 * Projet : library-management-system
 * Date : 02/01/2026
 */

@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionPlanMapper mapper;
    private final UserService userService;

    @Override
    public SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO planDTO) throws Exception {
        if (planRepository.existsByPlanCode(planDTO.getPlanCode())) {
            throw new Exception("plan code is already exist");
        }
        SubscriptionPlan plan = mapper.toEntity(planDTO);

        User currentUser = userService.getCurrentUser();
        plan.setCreatedBy(currentUser.getFullName());
        plan.setUpdatedBy(currentUser.getFullName());
        SubscriptionPlan savedPlan = planRepository.save(plan);

        return mapper.toDTO(savedPlan);
    }

    @Override
    public SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO planDTO) throws Exception {
        SubscriptionPlan existingPlan = planRepository.findById(planId).orElseThrow(
                ()-> new Exception("Plan not found!")
        );
        mapper.updateEntityFromDTO(existingPlan, planDTO);
        User currentUser = userService.getCurrentUser();
        existingPlan.setUpdatedBy(currentUser.getFullName());
        SubscriptionPlan updatedPlan = planRepository.save(existingPlan);

        return mapper.toDTO(updatedPlan);
    }

    @Override
    public void deleteSubscription(Long planId) throws Exception {
        SubscriptionPlan existingPlan = planRepository.findById(planId).orElseThrow(
                ()-> new Exception("Plan not found!")
        );
        planRepository.delete(existingPlan);

    }

    @Override
    public List<SubscriptionPlanDTO> getAllSubscriptionPlan() {
        List<SubscriptionPlan> planList = planRepository.findAll();

        return planList.stream().map(
                mapper::toDTO
        ).collect(Collectors.toList());
    }
}
