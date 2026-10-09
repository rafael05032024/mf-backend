package com.fmarket.service;

import com.fmarket.dto.LoginRequestDTO;
import com.fmarket.dto.LoginResponseDTO;
import com.fmarket.exception.InvalidPasswordException;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.UserModel;
import com.fmarket.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;

@ApplicationScoped
public class LoginService {

    @Inject
    UserRepository userRepository;

    @ConfigProperty(name = "app.jwt.issuer")
    String issuer;

    @ConfigProperty(name = "app.jwt.secret")
    String secret;

    @ConfigProperty(name = "app.jwt.expiration")
    Duration expiration;

    public LoginResponseDTO login(LoginRequestDTO request) {
        UserModel user = userRepository.findActiveByEmail(request.email().trim())
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        if (!BcryptUtil.matches(request.password(), user.password)) {
            throw new InvalidPasswordException("Senha inválida");
        }

        return LoginResponseDTO.ofToken(generateToken(user), user.verified);
    }

    private String generateToken(UserModel user) {
        return Jwt.issuer(issuer)
                .subject(String.valueOf(user.id))
                .expiresIn(expiration)
                .signWithSecret(secret);
    }
}
