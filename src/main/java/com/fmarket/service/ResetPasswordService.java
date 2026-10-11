package com.fmarket.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fmarket.exception.UserNotFoundException;
import com.fmarket.exception.VerificationCodeException;
import com.fmarket.model.ResetPassRequestModel;
import com.fmarket.model.SessionModel;
import com.fmarket.model.UserModel;
import com.fmarket.repository.ResetPassRequestRepository;
import com.fmarket.repository.SessionRepository;
import com.fmarket.repository.UserRepository;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class ResetPasswordService {

    private static final String SESSION_NOT_FOUND = "Sessão não encontrada";

    @Inject
    UserRepository userRepository;

    @Inject
    SessionRepository sessionRepository;

    @Inject
    ResetPassRequestRepository resetPassRequestRepository;

    @Transactional
    public void resetBySession(String rawSessionId, String password) {
        LocalDateTime now = LocalDateTime.now();

        UUID sessionId = parseSessionId(rawSessionId);
        SessionModel session = sessionRepository.findByIdOptional(sessionId)
                .orElseThrow(() -> new VerificationCodeException(SESSION_NOT_FOUND, Response.Status.NOT_FOUND));

        if (session.expireAt.isBefore(now)) {
            throw new VerificationCodeException("Sessão já está expirada", Response.Status.BAD_REQUEST);
        }

        ResetPassRequestModel resetPass = resetPassRequestRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new VerificationCodeException(SESSION_NOT_FOUND, Response.Status.NOT_FOUND));

        UserModel user = userRepository.findActiveByEmail(resetPass.email)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        changePassword(user, password, now);

        // a sessão só vale para uma troca de senha
        session.expireAt = now;
        session.updatedAt = now;
    }

    @Transactional
    public void resetByUser(Long userId, String password) {
        UserModel user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        changePassword(user, password, LocalDateTime.now());
    }

    private void changePassword(UserModel user, String password, LocalDateTime now) {
        user.password = BcryptUtil.bcryptHash(password);
        user.updatedAt = now;
    }

    private UUID parseSessionId(String rawSessionId) {
        try {
            return UUID.fromString(rawSessionId.trim());
        } catch (IllegalArgumentException e) {
            throw new VerificationCodeException(SESSION_NOT_FOUND, Response.Status.NOT_FOUND);
        }
    }
}
