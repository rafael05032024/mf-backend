package com.fmarket.security;

import com.fmarket.exception.InvalidTokenException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.keys.HmacKey;
import org.jose4j.jwa.AlgorithmConstraints;

import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Provider
@Authenticated
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    private static final String BEARER = "Bearer ";

    private final JwtConsumer consumer;

    public JwtAuthenticationFilter(
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

    @Override
    public void filter(ContainerRequestContext context) {
        String header = context.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.regionMatches(true, 0, BEARER, 0, BEARER.length())) {
            throw new InvalidTokenException("Token ausente");
        }

        String subject;
        try {
            subject = consumer.processToClaims(header.substring(BEARER.length()).trim()).getSubject();
            Long.parseLong(subject);
        } catch (InvalidJwtException | org.jose4j.jwt.MalformedClaimException | NumberFormatException e) {
            throw new InvalidTokenException("Token inválido ou expirado");
        }

        Principal principal = () -> subject;
        context.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return principal;
            }

            @Override
            public boolean isUserInRole(String role) {
                return false;
            }

            @Override
            public boolean isSecure() {
                return context.getSecurityContext().isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return "Bearer";
            }
        });
    }
}
