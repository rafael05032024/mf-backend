package com.fmarket.service;

import java.time.LocalDateTime;

import com.fmarket.dto.CreatePlanRequestDTO;
import com.fmarket.dto.CreatePlanResponseDTO;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.PlanModel;
import com.fmarket.repository.PlanRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreatePlanService {

    @Inject
    UserRepository userRepository;

    @Inject
    PlanRepository planRepository;

    @Transactional
    public CreatePlanResponseDTO create(Long userId, CreatePlanRequestDTO request) {
        userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        PlanModel plan = planRepository.findByProducer(userId).orElse(null);
        if (plan == null) {
            plan = new PlanModel();
            plan.producer = userId;
            plan.value = request.value();
            plan.createdAt = LocalDateTime.now();
            plan.updatedAt = plan.createdAt;
            planRepository.persist(plan);
        } else {
            plan.value = request.value();
            plan.updatedAt = LocalDateTime.now();
        }

        return new CreatePlanResponseDTO(plan.id, plan.value);
    }
}
