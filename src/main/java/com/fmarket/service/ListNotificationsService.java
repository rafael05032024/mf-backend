package com.fmarket.service;

import com.fmarket.dto.ListNotificationsResponseDTO;
import com.fmarket.dto.ListNotificationsResponseDTO.NotificationDTO;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.repository.NotificationRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ListNotificationsService {

    @Inject
    UserRepository userRepository;

    @Inject
    NotificationRepository notificationRepository;

    public ListNotificationsResponseDTO list(Long userId) {
        userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        var notifications = notificationRepository.findByUserId(userId).stream()
                .map(n -> new NotificationDTO(n.id, n.title, n.text, n.icon, n.type, n.createdAt, n.readedAt))
                .toList();

        return new ListNotificationsResponseDTO(notifications);
    }
}
