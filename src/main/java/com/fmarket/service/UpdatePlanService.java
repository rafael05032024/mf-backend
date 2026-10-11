package com.fmarket.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.fmarket.dto.UpdatePlanRequestDTO;
import com.fmarket.exception.ActionForbiddenException;
import com.fmarket.exception.InvalidPlanValueException;
import com.fmarket.exception.PlanNotFoundException;
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
    private static final BigDecimal MAX_VALUE = new BigDecimal("300.00");
    private static final BigDecimal LEVEL_2_MAX_VALUE = new BigDecimal("150.00");
    private static final int MIN_LEVEL = 2;
    private static final int LEVEL_LIMITED = 2;

    @Inject
    UserRepository userRepository;

    @Inject
    PlanRepository planRepository;

    @Transactional
    public void update(Long userId, UpdatePlanRequestDTO request) {
        UserModel user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        if (user.level == null || user.level < MIN_LEVEL) {
            throw new ActionForbiddenException("Usuário não tem level para executar a ação");
        }

        PlanModel plan = planRepository.findByProducer(userId)
                .orElseThrow(() -> new PlanNotFoundException("Plano inexistente"));

        BigDecimal value = request.value().setScale(2, RoundingMode.HALF_UP);
        if (value.compareTo(MIN_VALUE) < 0) {
            throw new InvalidPlanValueException(
                    "Não é possível alterar o valor da assinatura pois o valor está abaixo do limite permitido");
        }
        if (value.compareTo(MAX_VALUE) > 0) {
            throw new InvalidPlanValueException(
                    "Não é possível alterar o valor da assinatura pois o valor está acima do limite permitido");
        }
        if (user.level == LEVEL_LIMITED && value.compareTo(LEVEL_2_MAX_VALUE) > 0) {
            throw new InvalidPlanValueException(
                    "O valor da assinatura não pode ser alterado: o máximo permitido para o seu level é R$ 150,00");
        }

        plan.value = value;
        plan.updatedAt = LocalDateTime.now();
    }
}
