package com.fmarket.service;

import com.fmarket.dto.CreateAccountRequestDTO;
import com.fmarket.exception.BusinessConflictException;
import com.fmarket.model.UserModel;
import com.fmarket.model.WalletModel;
import com.fmarket.repository.UserRepository;
import com.fmarket.repository.WalletRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

@ApplicationScoped
public class CreateAccountService {

    @Inject
    UserRepository userRepository;

    @Inject
    WalletRepository walletRepository;

    @Transactional
    public void create(CreateAccountRequestDTO request) {
        String email = request.email().trim();
        String profile = request.profile().trim();

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
}
