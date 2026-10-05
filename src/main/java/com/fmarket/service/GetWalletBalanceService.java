package com.fmarket.service;

import com.fmarket.dto.GetWalletBalanceResponseDTO;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.repository.UserRepository;
import com.fmarket.repository.WalletRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetWalletBalanceService {

    @Inject
    UserRepository userRepository;

    @Inject
    WalletRepository walletRepository;

    public GetWalletBalanceResponseDTO get(Long userId) {
        userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        var wallet = walletRepository.findByOwner(userId)
                .orElseThrow(() -> new IllegalStateException("Carteira não encontrada para o usuário " + userId));

        return new GetWalletBalanceResponseDTO(wallet.balance);
    }
}
