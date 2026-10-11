package com.fmarket.service;

import java.time.LocalDateTime;

import com.fmarket.exception.ActionForbiddenException;
import com.fmarket.exception.NotificationNotFoundException;
import com.fmarket.model.NotificationModel;
import com.fmarket.repository.NotificationRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MarkNotificationAsReadService {

    @Inject
    NotificationRepository notificationRepository;

    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        NotificationModel notification = notificationRepository.findByIdOptional(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notificação inexistente"));

        if (!userId.equals(notification.userId)) {
            throw new ActionForbiddenException("Ação não permitida");
        }

        notification.readedAt = LocalDateTime.now();
    }
}
