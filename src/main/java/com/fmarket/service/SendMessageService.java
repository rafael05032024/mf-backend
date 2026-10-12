package com.fmarket.service;

import java.time.LocalDateTime;
import java.util.Map;

import com.fmarket.dto.EventResponseDTO;
import com.fmarket.dto.SendMessageRequestDTO;
import com.fmarket.dto.SendMessageResponseDTO;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.MessageModel;
import com.fmarket.model.UserModel;
import com.fmarket.repository.MessageRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SendMessageService {

    private static final String EVENT_NEW_MESSAGE = "new_message";

    @Inject
    UserRepository userRepository;

    @Inject
    MessageRepository messageRepository;

    @Inject
    EventService eventService;

    @Inject
    TransactionSynchronizationRegistry transactionRegistry;

    @Transactional
    public SendMessageResponseDTO send(Long fromUserId, SendMessageRequestDTO request) {
        // Remetente nulo = suporte (dono do sistema respondendo).
        if (fromUserId != null) {
            userRepository.findActiveById(fromUserId)
                    .orElseThrow(() -> new UserNotFoundException("Remetente inexistente"));
        }

        // Sem destinatário = contato com o suporte: grava a mensagem e não dispara evento.
        UserModel recipient = null;
        if (request.to() != null) {
            recipient = userRepository.findActiveById(request.to())
                    .orElseThrow(() -> new UserNotFoundException("Destinatário inexistente"));
        }

        LocalDateTime now = LocalDateTime.now();
        MessageModel message = new MessageModel();
        message.fromUserId = fromUserId;
        message.toUserId = request.to();
        message.text = request.text();
        message.createdAt = now;
        message.updatedAt = now;
        messageRepository.persist(message);

        if (recipient != null) {
            publishAfterCommit(recipient.profile, message);
        }

        return new SendMessageResponseDTO(message.id, message.fromUserId, message.toUserId, message.text,
                message.createdAt);
    }

    // Só avisa o destinatário depois do commit, para não anunciar uma mensagem que sofreu rollback.
    private void publishAfterCommit(String profile, MessageModel message) {
        Map<String, Object> data = Map.of(
                "id", message.id,
                "from", message.fromUserId == null ? 0L : message.fromUserId,
                "text", message.text,
                "createdAt", message.createdAt.toString());
        transactionRegistry.registerInterposedSynchronization(new Synchronization() {
            @Override
            public void beforeCompletion() {
            }

            @Override
            public void afterCompletion(int status) {
                if (status == Status.STATUS_COMMITTED) {
                    eventService.publish(profile, new EventResponseDTO(EVENT_NEW_MESSAGE, data));
                }
            }
        });
    }
}
