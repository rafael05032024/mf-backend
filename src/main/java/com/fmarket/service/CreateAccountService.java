package com.fmarket.service;

import com.fmarket.dto.CreateAccountRequestDTO;
import com.fmarket.exception.BusinessConflictException;
import com.fmarket.exception.VerificationCodeException;
import com.fmarket.model.RegistrationRequestModel;
import com.fmarket.model.UserModel;
import com.fmarket.model.WalletModel;
import com.fmarket.repository.RegistrationRequestRepository;
import com.fmarket.repository.UserRepository;
import com.fmarket.repository.WalletRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApplicationScoped
public class CreateAccountService {

    @Inject
    UserRepository userRepository;

    @Inject
    WalletRepository walletRepository;

    @Inject
    RegistrationRequestRepository registrationRequestRepository;

    @Transactional
    public void create(CreateAccountRequestDTO request) {
        String email = request.email().trim();
        String profile = request.profile().trim();

        validateVerificationCode(email, request.code().trim());

        if (userRepository.existsByEmail(email)) {
            throw new BusinessConflictException("E-mail já cadastrado");
        }
        if (userRepository.existsByProfile(profile)) {
            throw new BusinessConflictException("Profile já cadastrado");
        }

        UserModel user = new UserModel();
        user.name = request.name().trim();
        user.email = email;
        user.profile = profile;
        user.password = BcryptUtil.bcryptHash(request.password());
        userRepository.persist(user);

        WalletModel wallet = new WalletModel();
        wallet.owner = user.id;
        wallet.balance = BigDecimal.ZERO;
        walletRepository.persist(wallet);
    }

    private void validateVerificationCode(String email, String code) {
        RegistrationRequestModel registration = registrationRequestRepository.findLatestByEmail(email)
                .orElseThrow(() -> new VerificationCodeException(
                        "Não foi encontrado registro do cadastro", Response.Status.NOT_FOUND));

        if (registration.expireAt.isBefore(LocalDateTime.now())) {
            throw new VerificationCodeException(
                    "O código de verificação está expirado", Response.Status.BAD_REQUEST);
        }
        if (!registration.code.equals(code)) {
            throw new VerificationCodeException(
                    "Código de verificação inválido", Response.Status.BAD_REQUEST);
        }
    }
}
