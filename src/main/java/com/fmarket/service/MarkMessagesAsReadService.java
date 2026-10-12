package com.fmarket.service;

import java.time.LocalDateTime;

import com.fmarket.exception.UserNotFoundException;
import com.fmarket.repository.MessageRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MarkMessagesAsReadService {

    @Inject
    UserRepository userRepository;

    @Inject
    MessageRepository messageRepository;

    @Transactional
    public void markAllAsRead(Long userId) {
        userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        // Só as ainda não lidas, para preservar a data da primeira leitura.
        messageRepository.markAllAsRead(userId, LocalDateTime.now());
    }
}
