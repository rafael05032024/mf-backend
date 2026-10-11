package com.fmarket.service;

import java.time.LocalDateTime;

import com.fmarket.exception.BusinessConflictException;
import com.fmarket.exception.IncompleteProfileException;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.UserModel;
import com.fmarket.repository.LivenessRequestRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PartnerPreRegistrationService {

    private static final int PARTNER_LEVEL = 2;
    private static final int VERIFIED_LEVEL = 3;

    @Inject
    UserRepository userRepository;

    @Inject
    LivenessRequestRepository livenessRequestRepository;

    @Transactional
    public void register(Long userId) {
        UserModel user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        if (user.level != null && user.level == VERIFIED_LEVEL && Boolean.TRUE.equals(user.verified)) {
            throw new BusinessConflictException("Usuário já é verificado");
        }

        if (isBlank(user.document) || isBlank(user.personalName) || user.birthdate == null) {
            throw new IncompleteProfileException("Usuário não possui dados necessários para realizar a ação");
        }

        // O usuário optou por não verificar os documentos: descarta verificações pendentes.
        livenessRequestRepository.deleteUnprocessedByUser(userId);

        // Não rebaixa quem já tem level maior (ex.: 3).
        if (user.level == null || user.level < PARTNER_LEVEL) {
            user.level = PARTNER_LEVEL;
            user.updatedAt = LocalDateTime.now();
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
