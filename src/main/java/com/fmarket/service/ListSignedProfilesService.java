package com.fmarket.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fmarket.dto.ListSignedProfilesResponseDTO;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.SignatureModel;
import com.fmarket.model.UserModel;
import com.fmarket.repository.SignatureRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ListSignedProfilesService {

    @Inject
    UserRepository userRepository;

    @Inject
    SignatureRepository signatureRepository;

    public List<ListSignedProfilesResponseDTO> list(Long userId) {
        userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        List<SignatureModel> signatures = signatureRepository.listActiveBySubscriber(userId, LocalDateTime.now());
        if (signatures.isEmpty()) {
            return List.of();
        }

        List<Long> producerIds = signatures.stream().map(s -> s.producer).toList();
        Map<Long, UserModel> producers = userRepository.findByIds(producerIds).stream()
                .collect(Collectors.toMap(u -> u.id, Function.identity()));

        return signatures.stream()
                .filter(s -> producers.containsKey(s.producer))
                .map(s -> {
                    UserModel producer = producers.get(s.producer);
                    return new ListSignedProfilesResponseDTO(producer.profile, producer.thumb, s.expireAt);
                })
                .toList();
    }
}
