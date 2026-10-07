package com.fmarket.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.fmarket.dto.CreateRechargeRequestDTO;
import com.fmarket.dto.CreateRechargeResponseDTO;
import com.fmarket.dto.GeneratePixQrCodeRequestDTO;
import com.fmarket.dto.GeneratePixQrCodeResponseDTO;
import com.fmarket.exception.InvalidRechargeValueException;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.RechargeRequestModel;
import com.fmarket.provider.PaymentGatewayProvider;
import com.fmarket.repository.RechargeRequestRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateRechargeService {

    private static final BigDecimal MIN_VALUE = new BigDecimal("15.00");
    private static final BigDecimal MAX_VALUE = new BigDecimal("150.00");
    private static final long QR_CODE_EXPIRATION_MINUTES = 30;

    @Inject
    UserRepository userRepository;

    @Inject
    RechargeRequestRepository rechargeRequestRepository;

    @Inject
    PaymentGatewayProvider paymentGatewayProvider;

    @Transactional
    public CreateRechargeResponseDTO create(Long userId, CreateRechargeRequestDTO request) {
        userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        BigDecimal value = request.value().setScale(2, RoundingMode.HALF_UP);
        if (value.compareTo(MIN_VALUE) < 0 || value.compareTo(MAX_VALUE) > 0) {
            throw new InvalidRechargeValueException("O valor da recarga deve estar entre R$ 15,00 e R$ 150,00");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plusMinutes(QR_CODE_EXPIRATION_MINUTES);
        GeneratePixQrCodeResponseDTO qrCode = paymentGatewayProvider.generatePixQrCode(
                new GeneratePixQrCodeRequestDTO(value, "Recarga de footcoins",
                        expiredAt));

        RechargeRequestModel recharge = new RechargeRequestModel();
        recharge.userId = userId;
        recharge.value = value;
        recharge.reference = qrCode.reference();
        recharge.expiredAt = expiredAt;
        recharge.createdAt = now;
        rechargeRequestRepository.persist(recharge);

        return new CreateRechargeResponseDTO(recharge.id, qrCode.qrCodeImage(), qrCode.payload());
    }
}
