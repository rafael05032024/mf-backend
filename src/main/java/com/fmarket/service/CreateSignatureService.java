package com.fmarket.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.fmarket.dto.CreateSignatureRequestDTO;
import com.fmarket.dto.CreateSignatureResponseDTO;
import com.fmarket.exception.BusinessConflictException;
import com.fmarket.exception.InactiveUserException;
import com.fmarket.exception.InsufficientBalanceException;
import com.fmarket.exception.SelfSignatureException;
import com.fmarket.exception.UnverifiedPublisherException;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.NotificationModel;
import com.fmarket.model.PlanModel;
import com.fmarket.model.SignatureModel;
import com.fmarket.model.TransactionModel;
import com.fmarket.model.TransactionType;
import com.fmarket.model.UserModel;
import com.fmarket.model.WalletModel;
import com.fmarket.repository.NotificationRepository;
import com.fmarket.repository.PlanRepository;
import com.fmarket.repository.SignatureRepository;
import com.fmarket.repository.TransactionRepository;
import com.fmarket.repository.UserRepository;
import com.fmarket.repository.WalletRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateSignatureService {

    private static final int SIGNATURE_DURATION_DAYS = 30;
    // Comissão da plataforma sobre o valor do plano (valores em footcoins, 1 R$ = 5 ft).
    private static final BigDecimal PLATFORM_FEE_RATE = new BigDecimal("0.10");

    @Inject
    UserRepository userRepository;

    @Inject
    SignatureRepository signatureRepository;

    @Inject
    PlanRepository planRepository;

    @Inject
    WalletRepository walletRepository;

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    NotificationRepository notificationRepository;

    @Transactional
    public CreateSignatureResponseDTO create(Long subscriberId, CreateSignatureRequestDTO request) {
        UserModel subscriber = userRepository.findActiveById(subscriberId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        UserModel producer = userRepository.findByProfile(request.producer().trim())
                .orElseThrow(() -> new UserNotFoundException("Produtor inexistente"));

        if (subscriberId.equals(producer.id)) {
            throw new SelfSignatureException("Você não pode assinar a si mesmo");
        }

        if (producer.deletedAt != null) {
            throw new InactiveUserException("Usuário inativo");
        }

        if (!Boolean.TRUE.equals(producer.verified)) {
            throw new UnverifiedPublisherException("Produtor não é verificado");
        }

        PlanModel plan = planRepository.findByProducer(producer.id)
                .orElseThrow(() -> new UserNotFoundException("Produtor não possui plano"));

        // Trava as carteiras sempre na mesma ordem (menor id primeiro) para evitar deadlock
        // e serializar requisições concorrentes do mesmo assinante.
        WalletModel subscriberWallet;
        WalletModel producerWallet;
        if (subscriberId < producer.id) {
            subscriberWallet = lockWallet(subscriberId);
            producerWallet = lockWallet(producer.id);
        } else {
            producerWallet = lockWallet(producer.id);
            subscriberWallet = lockWallet(subscriberId);
        }

        LocalDateTime now = LocalDateTime.now();
        if (signatureRepository.existsActive(subscriberId, producer.id, now)) {
            throw new BusinessConflictException("Você já possui uma assinatura vigente para este produtor");
        }

        BigDecimal price = plan.value;
        if (subscriberWallet.balance.compareTo(price) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente");
        }

        BigDecimal producerCredit = price.subtract(price.multiply(PLATFORM_FEE_RATE))
                .setScale(2, RoundingMode.HALF_UP);

        subscriberWallet.balance = subscriberWallet.balance.subtract(price);
        registerTransaction(subscriberWallet.id, TransactionType.DEBIT, price,
                "Assinatura do perfil @" + producer.profile, now);

        producerWallet.balance = producerWallet.balance.add(producerCredit);
        registerTransaction(producerWallet.id, TransactionType.CREDIT, producerCredit,
                "Assinatura de @" + subscriber.profile, now);

        SignatureModel signature = new SignatureModel();
        signature.subscriber = subscriberId;
        signature.producer = producer.id;
        signature.createdAt = now;
        signature.updatedAt = signature.createdAt;
        signature.expireAt = signature.createdAt.plusDays(SIGNATURE_DURATION_DAYS);
        signatureRepository.persist(signature);

        notify(subscriberId, "Assinatura de @" + producer.profile + " confirmada por "
                + SIGNATURE_DURATION_DAYS + " dias", now);
        notify(producer.id, "@" + subscriber.profile + " acabou de assinar seu perfil!!", now);

        return new CreateSignatureResponseDTO(signature.id, signature.producer, signature.subscriber,
                signature.expireAt);
    }

    private WalletModel lockWallet(Long owner) {
        return walletRepository.findByOwnerForUpdate(owner)
                .orElseThrow(() -> new IllegalStateException("Carteira inexistente para o usuário " + owner));
    }

    private void registerTransaction(Long walletId, TransactionType type, BigDecimal value,
            String description, LocalDateTime now) {
        TransactionModel transaction = new TransactionModel();
        transaction.walletId = walletId;
        transaction.type = type;
        transaction.value = value;
        transaction.description = description;
        transaction.createdAt = now;
        transaction.updatedAt = now;
        transactionRepository.persist(transaction);
    }

    private void notify(Long userId, String text, LocalDateTime now) {
        NotificationModel notification = new NotificationModel();
        notification.userId = userId;
        notification.text = text;
        notification.createdAt = now;
        notificationRepository.persist(notification);
    }
}
