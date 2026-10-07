package com.fmarket.service;

import java.time.LocalDateTime;

import com.fmarket.dto.GetSignatureCountResponseDTO;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.repository.SignatureRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetSignatureCountService {

    @Inject
    UserRepository userRepository;

    @Inject
    SignatureRepository signatureRepository;

    public GetSignatureCountResponseDTO get(Long userId) {
        userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        LocalDateTime now = LocalDateTime.now();
        return new GetSignatureCountResponseDTO(
                signatureRepository.countActiveBySubscriber(userId, now),
                signatureRepository.countActiveByProducer(userId, now));
    }
}
