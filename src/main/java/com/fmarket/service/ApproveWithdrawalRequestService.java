package com.fmarket.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.fmarket.dto.EventResponseDTO;
import com.fmarket.exception.BusinessConflictException;
import com.fmarket.exception.WithdrawalRequestNotFoundException;
import com.fmarket.model.NotificationModel;
import com.fmarket.model.TransactionModel;
import com.fmarket.model.TransactionType;
import com.fmarket.model.WalletModel;
import com.fmarket.model.WithdrawalRequestModel;
import com.fmarket.repository.NotificationRepository;
import com.fmarket.repository.TransactionRepository;
import com.fmarket.repository.WalletRepository;
import com.fmarket.repository.WithdrawalRequestRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ApproveWithdrawalRequestService {

    private static final String TRANSACTION_DESCRIPTION = "Solicitação de saque aprovada";
    private static final String NOTIFICATION_ICON = "Check";
    private static final int NOTIFICATION_TYPE = 1;
    private static final String NOTIFICATION_TITLE = "Solicitação de saque aprovada";
    private static final String EVENT_NEW_NOTIFICATION = "new_notification";
    private static final int STATUS_APPROVED = 200;

    @Inject
    WithdrawalRequestRepository withdrawalRequestRepository;

    @Inject
    WalletRepository walletRepository;

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    EventService eventService;

    @Inject
    TransactionSynchronizationRegistry transactionRegistry;

    @Transactional
    public void approve(Long withdrawalRequestId) {
        WithdrawalRequestModel withdrawalRequest = withdrawalRequestRepository.findByIdOptional(withdrawalRequestId)
                .orElseThrow(() -> new WithdrawalRequestNotFoundException("Solicitação de resgate não existe"));

        if (withdrawalRequest.proceededAt != null) {
            throw new BusinessConflictException("Solicitação de resgate já foi processada");
        }

        LocalDateTime now = LocalDateTime.now();
        withdrawalRequest.proceededAt = now;
        withdrawalRequest.updatedAt = now;
        withdrawalRequest.status = STATUS_APPROVED;

        WalletModel wallet = walletRepository.findByOwner(withdrawalRequest.userId)
                .orElseThrow(() -> new IllegalStateException(
                        "Carteira inexistente para o usuário " + withdrawalRequest.userId));

        registerTransaction(wallet.id, withdrawalRequest.value, now);

        NotificationModel notification = notify(withdrawalRequest.userId, withdrawalRequest.value,
                withdrawalRequest.addressKey, now);

        publishAfterCommit(withdrawalRequest.userId, notification);
    }

    private void registerTransaction(Long walletId, BigDecimal value, LocalDateTime now) {
        TransactionModel transaction = new TransactionModel();
        transaction.walletId = walletId;
        transaction.type = TransactionType.DEBIT;
        transaction.value = value;
        transaction.description = TRANSACTION_DESCRIPTION;
        transaction.createdAt = now;
        transaction.updatedAt = now;
        transactionRepository.persist(transaction);
    }

    private NotificationModel notify(Long userId, BigDecimal value, String addressKey, LocalDateTime now) {
        NotificationModel notification = new NotificationModel();
        notification.userId = userId;
        notification.title = NOTIFICATION_TITLE;
        notification.icon = NOTIFICATION_ICON;
        notification.type = NOTIFICATION_TYPE;
        notification.text = "A solicitação de saque no valor de " + value + " foi aprovada, bem como, já foi "
                + "providenciado a transferência do valor para chave PIX " + addressKey;
        notification.createdAt = now;
        notificationRepository.persist(notification);
        return notification;
    }

    // Só avisa o usuário depois do commit, para não anunciar uma aprovação que sofreu rollback.
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
