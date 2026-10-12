package com.fmarket.service;

import java.time.LocalDateTime;

import com.fmarket.dto.ListMessagesResponseDTO;
import com.fmarket.dto.ListMessagesResponseDTO.MessageDTO;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.repository.MessageRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ListMessagesService {

    private static final long TIMESTAMP_OFFSET_HOURS = 3;

    @Inject
    UserRepository userRepository;

    @Inject
    MessageRepository messageRepository;

    public ListMessagesResponseDTO list(Long userId) {
        userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        var messages = messageRepository.findByParticipant(userId).stream()
                .map(m -> new MessageDTO(m.id, orZero(m.fromUserId), orZero(m.toUserId), m.text,
                        minusOffset(m.createdAt), minusOffset(m.readedAt)))
                .toList();

        return new ListMessagesResponseDTO(messages);
    }

    // 0 representa o suporte (usuário nulo no banco).
    private static Long orZero(Long userId) {
        return userId == null ? 0L : userId;
    }

    // Ajuste de fuso: os timestamps são devolvidos 3h atrás.
    private static LocalDateTime minusOffset(LocalDateTime value) {
        return value == null ? null : value.minusHours(TIMESTAMP_OFFSET_HOURS);
    }
}
