package com.fmarket.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.fmarket.dto.CreateWithdrawalRequestDTO;
import com.fmarket.dto.EventResponseDTO;
import com.fmarket.exception.InsufficientBalanceException;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.NotificationModel;
import com.fmarket.model.TransactionModel;
import com.fmarket.model.TransactionType;
import com.fmarket.model.WalletModel;
import com.fmarket.model.WithdrawalRequestModel;
import com.fmarket.repository.NotificationRepository;
import com.fmarket.repository.TransactionRepository;
import com.fmarket.repository.UserRepository;
import com.fmarket.repository.WalletRepository;
import com.fmarket.repository.WithdrawalRequestRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateWithdrawalRequestService {

    private static final String TRANSACTION_DESCRIPTION = "Solicitação de saque em análise";
    private static final String NOTIFICATION_ICON = "Info";
    private static final int NOTIFICATION_TYPE = 3;
    private static final String NOTIFICATION_TITLE = "Solicitação de saque em análise!";
    private static final String NOTIFICATION_TEXT = "A sua solicitação de saque está em análise, em um prazo de "
            + "até 24h entraremos em contato, aguarde.";
    private static final String EVENT_NEW_NOTIFICATION = "new_notification";

    @Inject
    UserRepository userRepository;

    @Inject
    WalletRepository walletRepository;

    @Inject
    WithdrawalRequestRepository withdrawalRequestRepository;

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    EventService eventService;

    @Inject
    TransactionSynchronizationRegistry transactionRegistry;

    @Transactional
    public void create(Long userId, CreateWithdrawalRequestDTO request) {
        userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        WalletModel wallet = walletRepository.findByOwnerForUpdate(userId)
                .orElseThrow(() -> new IllegalStateException("Carteira inexistente para o usuário " + userId));

        BigDecimal value = request.value();
        if (wallet.balance.compareTo(value) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente");
        }

        LocalDateTime now = LocalDateTime.now();

        wallet.balance = wallet.balance.subtract(value);

        WithdrawalRequestModel withdrawalRequest = new WithdrawalRequestModel();
        withdrawalRequest.userId = userId;
        withdrawalRequest.value = value;
        withdrawalRequest.addressKey = request.addressKey();
        withdrawalRequest.createdAt = now;
        withdrawalRequest.updatedAt = now;
        withdrawalRequestRepository.persist(withdrawalRequest);

        registerTransaction(wallet.id, value, now);

        NotificationModel notification = notify(userId, now);

        publishAfterCommit(userId, notification);
    }

    private void registerTransaction(Long walletId, BigDecimal value, LocalDateTime now) {
        TransactionModel transaction = new TransactionModel();
        transaction.walletId = walletId;
        transaction.type = TransactionType.WITHDRAWAL;
        transaction.value = value;
        transaction.description = TRANSACTION_DESCRIPTION;
        transaction.createdAt = now;
        transaction.updatedAt = now;
        transactionRepository.persist(transaction);
    }

    private NotificationModel notify(Long userId, LocalDateTime now) {
        NotificationModel notification = new NotificationModel();
        notification.userId = userId;
        notification.title = NOTIFICATION_TITLE;
        notification.icon = NOTIFICATION_ICON;
        notification.type = NOTIFICATION_TYPE;
        notification.text = NOTIFICATION_TEXT;
        notification.createdAt = now;
        notificationRepository.persist(notification);
        return notification;
    }

    // Só avisa o usuário depois do commit, para não anunciar uma solicitação que sofreu rollback.
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
