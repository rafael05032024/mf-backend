package com.fmarket.service;

import java.time.format.DateTimeFormatter;

import com.fmarket.dto.CreateLivenessLinkResponseDTO;
import com.fmarket.dto.FraudPreventionExpectedDetailsDTO;
import com.fmarket.dto.FraudPreventionRequestDTO;
import com.fmarket.dto.FraudPreventionResponseDTO;
import com.fmarket.exception.IncompleteProfileException;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.LivenessRequestModel;
import com.fmarket.model.UserModel;
import com.fmarket.provider.FraudPreventionProvider;
import com.fmarket.repository.LivenessRequestRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateLivenessLinkService {

    @Inject
    UserRepository userRepository;

    @Inject
    LivenessRequestRepository livenessRequestRepository;

    @Inject
    FraudPreventionProvider fraudPreventionProvider;

    @Transactional
    public CreateLivenessLinkResponseDTO create(Long userId) {
        UserModel user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        if (isBlank(user.document) || isBlank(user.personalName) || user.birthdate == null) {
            throw new IncompleteProfileException(
                    "Documento, nome completo e data de nascimento são obrigatórios para a verificação");
        }

        String[] names = user.personalName.trim().split("\\s+", 2);
        String firstName = names[0];
        String lastName = names.length > 1 ? names[1] : "";

        FraudPreventionResponseDTO session = fraudPreventionProvider.createVerificationSession(
                new FraudPreventionRequestDTO(user.id, user.email),
                new FraudPreventionExpectedDetailsDTO(
                        firstName,
                        lastName,
                        user.birthdate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        user.document));

        LivenessRequestModel liveness = new LivenessRequestModel();
        liveness.userId = userId;
        liveness.sessionId = session.sessionId();
        livenessRequestRepository.persist(liveness);

        return new CreateLivenessLinkResponseDTO(liveness.id, session.verificationUrl());
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
