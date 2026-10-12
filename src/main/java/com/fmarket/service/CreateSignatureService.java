package com.fmarket.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;

import com.fmarket.dto.CreateSignatureRequestDTO;
import com.fmarket.dto.CreateSignatureResponseDTO;
import com.fmarket.dto.EventResponseDTO;
import com.fmarket.exception.BusinessConflictException;
import com.fmarket.exception.InactiveUserException;
import com.fmarket.exception.InsufficientBalanceException;
import com.fmarket.exception.SelfSignatureException;
import com.fmarket.exception.UnverifiedPublisherException;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.EarningModel;
import com.fmarket.model.NotificationModel;
import com.fmarket.model.PlanModel;
import com.fmarket.model.SignatureModel;
import com.fmarket.model.TransactionModel;
import com.fmarket.model.TransactionType;
import com.fmarket.model.UserModel;
import com.fmarket.model.WalletModel;
import com.fmarket.repository.EarningRepository;
import com.fmarket.repository.NotificationRepository;
import com.fmarket.repository.PlanRepository;
import com.fmarket.repository.SignatureRepository;
import com.fmarket.repository.TransactionRepository;
import com.fmarket.repository.UserRepository;
import com.fmarket.repository.WalletRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateSignatureService {

    private static final int SIGNATURE_DURATION_DAYS = 30;
    private static final String NOTIFICATION_TITLE = "Nova assinatura";
    private static final int NOTIFICATION_TYPE_SIGNATURE = 1;
    private static final String SUBSCRIBER_ICON = "UserPlus";
    private static final String PRODUCER_ICON = "BadgeDollarSign";
    private static final String EVENT_NEW_SUBSCRIPTION = "new_subscription";
    private static final String EVENT_NEW_SUBSCRIBER = "new_subscriber";
    private static final int PRODUCER_MIN_LEVEL = 2;
    private static final int PRODUCER_LIMITED_LEVEL = 2;
    private static final long PRODUCER_LIMITED_MAX_SIGNATURES = 30;
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
    EarningRepository earningRepository;

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    EventService eventService;

    @Inject
    TransactionSynchronizationRegistry transactionRegistry;

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

        if (producer.level == null || producer.level < PRODUCER_MIN_LEVEL) {
            throw new UnverifiedPublisherException("Publisher não é produtor de conteúdo");
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

        if (producer.level == PRODUCER_LIMITED_LEVEL
                && signatureRepository.countActiveByProducer(producer.id, now) >= PRODUCER_LIMITED_MAX_SIGNATURES) {
            throw new BusinessConflictException("A assinatura não pode ser efetivada");
        }

        BigDecimal price = plan.value;
        if (subscriberWallet.balance.compareTo(price) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente");
        }

        BigDecimal platformEarning = price.multiply(PLATFORM_FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal producerCredit = price.subtract(platformEarning).setScale(2, RoundingMode.HALF_UP);

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

        registerEarning(signature.id, platformEarning, producerCredit, now);

        NotificationModel subscriberNotification = notify(subscriberId, SUBSCRIBER_ICON,
                "Assinatura de @" + producer.profile + " confirmada por " + SIGNATURE_DURATION_DAYS + " dias", now);
        NotificationModel producerNotification = notify(producer.id, PRODUCER_ICON,
                "@" + subscriber.profile + " acabou de assinar seu perfil!!", now);

        publishAfterCommit(subscriber.profile, EVENT_NEW_SUBSCRIPTION, subscriberNotification);
        publishAfterCommit(producer.profile, EVENT_NEW_SUBSCRIBER, producerNotification);

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

    private void registerEarning(Long signatureId, BigDecimal platformValue, BigDecimal producerValue,
            LocalDateTime now) {
        EarningModel earning = new EarningModel();
        earning.subscriptionId = signatureId;
        earning.valuePlatform = platformValue;
        earning.valueProducer = producerValue;
        earning.createdAt = now;
        earning.updatedAt = now;
        earningRepository.persist(earning);
    }

    private NotificationModel notify(Long userId, String icon, String text, LocalDateTime now) {
        NotificationModel notification = new NotificationModel();
        notification.userId = userId;
        notification.title = NOTIFICATION_TITLE;
        notification.icon = icon;
        notification.type = NOTIFICATION_TYPE_SIGNATURE;
        notification.text = text;
        notification.createdAt = now;
        notificationRepository.persist(notification);
        return notification;
    }

    // Só avisa o frontend depois do commit, para não anunciar uma assinatura que sofreu rollback.
    private void publishAfterCommit(String profile, String eventType, NotificationModel notification) {
        Map<String, Object> data = Map.of(
                "id", notification.id,
                "title", notification.title,
                "text", notification.text,
                "icon", notification.icon,
                "type", notification.type);
        transactionRegistry.registerInterposedSynchronization(new Synchronization() {
            @Override
            public void beforeCompletion() {
            }

            @Override
            public void afterCompletion(int status) {
                if (status == Status.STATUS_COMMITTED) {
                    eventService.publish(profile, new EventResponseDTO(eventType, data));
                }
            }
        });
    }
}
