package com.fmarket.security;

import java.nio.charset.StandardCharsets;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.keys.HmacKey;

import com.fmarket.exception.InvalidTokenException;

import jakarta.enterprise.context.ApplicationScoped;

/** Valida o JWT do header Authorization e extrai o id do usuário. */
@ApplicationScoped
public class JwtTokenReader {

    private static final String BEARER = "Bearer ";

    private final JwtConsumer consumer;

    public JwtTokenReader(
            @ConfigProperty(name = "app.jwt.issuer") String issuer,
            @ConfigProperty(name = "app.jwt.secret") String secret) {
        this.consumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setRequireSubject()
                .setExpectedIssuer(issuer)
                .setVerificationKey(new HmacKey(secret.getBytes(StandardCharsets.UTF_8)))
                .setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT,
                        AlgorithmIdentifiers.HMAC_SHA256)
                .build();
    }

    /** Retorna o id do usuário do header Authorization ou lança {@link InvalidTokenException}. */
    public Long readUserId(String authorizationHeader) {
        if (authorizationHeader == null
                || !authorizationHeader.regionMatches(true, 0, BEARER, 0, BEARER.length())) {
            throw new InvalidTokenException("Token ausente");
        }
        return readUserIdFromToken(authorizationHeader.substring(BEARER.length()).trim());
    }

    /** Retorna o id do usuário de um JWT puro (ex.: cookie) ou lança {@link InvalidTokenException}. */
    public Long readUserIdFromToken(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException("Token ausente");
        }

        try {
            return Long.parseLong(consumer.processToClaims(token.trim()).getSubject());
        } catch (InvalidJwtException | MalformedClaimException | NumberFormatException e) {
            throw new InvalidTokenException("Token inválido ou expirado");
        }
    }
}
