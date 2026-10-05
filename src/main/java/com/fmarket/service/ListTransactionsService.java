package com.fmarket.service;

import com.fmarket.dto.ListTransactionsResponseDTO;
import com.fmarket.dto.ListTransactionsResponseDTO.TransactionDTO;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.repository.TransactionRepository;
import com.fmarket.repository.UserRepository;
import com.fmarket.repository.WalletRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ListTransactionsService {

    @Inject
    UserRepository userRepository;

    @Inject
    WalletRepository walletRepository;

    @Inject
    TransactionRepository transactionRepository;

    public ListTransactionsResponseDTO list(Long userId) {
        userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        var wallet = walletRepository.findByOwner(userId)
                .orElseThrow(() -> new IllegalStateException("Carteira não encontrada para o usuário " + userId));

        var transactions = transactionRepository.findByWalletId(wallet.id).stream()
                .map(t -> new TransactionDTO(t.id, t.type.getCode(), t.value, t.description))
                .toList();

        return new ListTransactionsResponseDTO(wallet.balance, transactions);
    }
}
