package com.fmarket.service;

import java.time.LocalDateTime;

import com.fmarket.dto.VerifyPasswordResetCodeRequestDTO;
import com.fmarket.dto.VerifyPasswordResetCodeResponseDTO;
import com.fmarket.exception.VerificationCodeException;
import com.fmarket.model.ResetPassRequestModel;
import com.fmarket.model.SessionModel;
import com.fmarket.repository.ResetPassRequestRepository;
import com.fmarket.repository.SessionRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class VerifyPasswordResetCodeService {

    private static final int SESSION_EXPIRATION_MINUTES = 10;

    @Inject
    ResetPassRequestRepository resetPassRequestRepository;

    @Inject
    SessionRepository sessionRepository;

    @Transactional
    public VerifyPasswordResetCodeResponseDTO verify(VerifyPasswordResetCodeRequestDTO request) {
        LocalDateTime now = LocalDateTime.now();

        ResetPassRequestModel resetPass = resetPassRequestRepository
                .findLatestValidByEmail(request.email().trim(), now)
                .orElseThrow(() -> new VerificationCodeException(
                        "Código de verificação inválido", Response.Status.BAD_REQUEST));

        if (!resetPass.code.equals(request.code().trim())) {
            throw new VerificationCodeException("Código de verificação inválido", Response.Status.BAD_REQUEST);
        }

        SessionModel session = new SessionModel();
        session.expireAt = now.plusMinutes(SESSION_EXPIRATION_MINUTES);
        sessionRepository.persist(session);

        resetPass.sessionId = session.sessionId;
        resetPass.updatedAt = now;

        return new VerifyPasswordResetCodeResponseDTO(session.sessionId);
    }
}
