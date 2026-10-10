package com.fmarket.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.fmarket.dto.UpdatePlanRequestDTO;
import com.fmarket.exception.InvalidPlanValueException;
import com.fmarket.exception.PlanNotFoundException;
import com.fmarket.exception.UnverifiedPublisherException;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.PlanModel;
import com.fmarket.model.UserModel;
import com.fmarket.repository.PlanRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UpdatePlanService {

    private static final BigDecimal MIN_VALUE = new BigDecimal("15.00");
    private static final BigDecimal MAX_VALUE = new BigDecimal("150.00");

    @Inject
    UserRepository userRepository;

    @Inject
    PlanRepository planRepository;

    @Transactional
    public void update(Long userId, UpdatePlanRequestDTO request) {
        UserModel user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        if (!Boolean.TRUE.equals(user.verified)) {
            throw new UnverifiedPublisherException("Usuário não é verificado");
        }

        PlanModel plan = planRepository.findByProducer(userId)
                .orElseThrow(() -> new PlanNotFoundException("Plano inexistente"));

        BigDecimal value = request.value().setScale(2, RoundingMode.HALF_UP);
        if (value.compareTo(MIN_VALUE) < 0 || value.compareTo(MAX_VALUE) > 0) {
            throw new InvalidPlanValueException("O valor do plano deve estar entre R$ 15,00 e R$ 150,00");
        }

        plan.value = value;
        plan.updatedAt = LocalDateTime.now();
    }
}
