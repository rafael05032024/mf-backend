package com.fmarket.service;

import java.time.LocalDateTime;
import java.util.Map;

import com.fmarket.dto.EventResponseDTO;

import com.fmarket.exception.BusinessConflictException;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.NotificationModel;
import com.fmarket.model.UserModel;
import com.fmarket.repository.LivenessRequestRepository;
import com.fmarket.repository.NotificationRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PartnerPreRegistrationService {

    private static final int PARTNER_LEVEL = 2;
    private static final int VERIFIED_LEVEL = 3;
    private static final String NOTIFICATION_TITLE = "Cadastro realizado sem verificação de documentos";
    private static final String NOTIFICATION_TEXT = "Você concluiu seu cadastro sem verificar os documentos, "
            + "dessa forma ficará sujeito a limite de postagens e assinaturas, você pode fazer a verificação "
            + "dos seus documentos quando quiser, bastar acessar as configurações e procurar por "
            + "\"Verificar conta\"";
    private static final String NOTIFICATION_ICON = "Info";
    private static final int NOTIFICATION_TYPE = 3;
    private static final String EVENT_NEW_NOTIFICATION = "new_notification";

    @Inject
    UserRepository userRepository;

    @Inject
    LivenessRequestRepository livenessRequestRepository;

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    EventService eventService;

    @Inject
    TransactionSynchronizationRegistry transactionRegistry;

    @Transactional
    public void register(Long userId) {
        UserModel user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        if (user.level != null && user.level == VERIFIED_LEVEL && Boolean.TRUE.equals(user.verified)) {
            throw new BusinessConflictException("Usuário já é verificado");
        }

        // O usuário optou por não verificar os documentos: descarta verificações
        // pendentes.
        livenessRequestRepository.deleteUnprocessedByUser(userId);

        // Não rebaixa quem já tem level maior (ex.: 3).
        if (user.level == null || user.level < PARTNER_LEVEL) {
            user.level = PARTNER_LEVEL;
            user.updatedAt = LocalDateTime.now();
        }

        createUnverifiedNotification(userId);
    }

    private void createUnverifiedNotification(Long userId) {
        NotificationModel notification = new NotificationModel();
        notification.userId = userId;
        notification.title = NOTIFICATION_TITLE;
        notification.text = NOTIFICATION_TEXT;
        notification.icon = NOTIFICATION_ICON;
        notification.type = NOTIFICATION_TYPE;
        notificationRepository.persist(notification);

        publishAfterCommit(userId, notification);
    }

    // Só avisa o usuário depois do commit, para não anunciar uma notificação que sofreu rollback.
    private void publishAfterCommit(Long userId, NotificationModel notification) {
        Map<String, Object> data = Map.of(
                "id", notification.id,
                "title", notification.title,
                "text", notification.text,
                "icon", notification.icon,
                "type", notification.type,
                "createdAt", notification.createdAt.toString());
        transactionRegistry.registerInterposedSynchronization(new Synchronization() {
            @Override
            public void beforeCompletion() {
            }

            @Override
            public void afterCompletion(int status) {
                if (status == Status.STATUS_COMMITTED) {
                    eventService.publish(userId, new EventResponseDTO(EVENT_NEW_NOTIFICATION, data));
                }
            }
        });
    }
}
