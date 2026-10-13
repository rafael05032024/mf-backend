package com.fmarket.service;

import java.time.LocalDateTime;

import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.UserModel;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PauseAccountService {

    @Inject
    UserRepository userRepository;

    @Transactional
    public void pause(Long userId) {
        UserModel user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        // Idempotente: mantém a data da primeira pausa.
        if (user.pausedAt == null) {
            user.pausedAt = LocalDateTime.now();
        }
    }
}
