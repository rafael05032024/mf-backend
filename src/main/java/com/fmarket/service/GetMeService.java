package com.fmarket.service;

import java.time.LocalDateTime;

import com.fmarket.dto.GetMeResponseDTO;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.repository.SignatureRepository;
import com.fmarket.repository.UserRepository;
import com.fmarket.repository.WalletRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetMeService {

    @Inject
    UserRepository userRepository;

    @Inject
    WalletRepository walletRepository;

    @Inject
    SignatureRepository signatureRepository;

    public GetMeResponseDTO get(Long userId) {
        var user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        var wallet = walletRepository.findByOwner(userId)
                .orElseThrow(() -> new IllegalStateException("Carteira não encontrada para o usuário " + userId));

        long subscriptions = signatureRepository.countActiveBySubscriber(userId, LocalDateTime.now());

        return new GetMeResponseDTO(user.name, user.profile, user.thumb, wallet.balance, subscriptions,
                user.verified);
    }
}
