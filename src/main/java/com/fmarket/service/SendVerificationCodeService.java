package com.fmarket.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fmarket.dto.SendVerificationCodeRequestDTO;
import com.fmarket.exception.VerificationCodeLimitException;
import com.fmarket.model.RegistrationRequestModel;
import com.fmarket.provider.EmailProvider;
import com.fmarket.repository.RegistrationRequestRepository;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SendVerificationCodeService {

    private static final String TEMPLATE_PATH = "/templates/verification-code-email.html";
    private static final String SUBJECT = "Código de verificação – My Foot";
    private static final int CODE_LENGTH = 6;
    private static final int MAX_REQUESTS_PER_EMAIL = 10;
    private static final int EXPIRATION_MINUTES = 10;
    private static final DateTimeFormatter EXPIRATION_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final SecureRandom random = new SecureRandom();

    private String template;

    @Inject
    RegistrationRequestRepository registrationRequestRepository;

    @Inject
    EmailProvider emailProvider;

    @PostConstruct
    void loadTemplate() {
        try (InputStream is = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (is == null) {
                throw new IllegalStateException("Template de e-mail não encontrado: " + TEMPLATE_PATH);
            }
            template = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar template de e-mail", e);
        }
    }

    @Transactional
    public void send(SendVerificationCodeRequestDTO request) {
        if (registrationRequestRepository.countByEmail(request.email()) >= MAX_REQUESTS_PER_EMAIL) {
            throw new VerificationCodeLimitException(
                    "Limite de solicitações de código atingido para este e-mail");
        }

        String code = generateCode();
        LocalDateTime expireAt = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);

        RegistrationRequestModel registration = new RegistrationRequestModel();
        registration.email = request.email();
        registration.code = code;
        registration.expireAt = expireAt;
        registrationRequestRepository.persist(registration);

        emailProvider.sendEmail(request.email(), SUBJECT, render(request.name(), code, expireAt));
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    private String render(String name, String code, LocalDateTime expireAt) {
        String html = template;
        for (int i = 0; i < code.length(); i++) {
            html = html.replace("{{CODIGO_" + (i + 1) + "}}", String.valueOf(code.charAt(i)));
        }
        return html
                .replace("{{CODIGO}}", code)
                .replace("{{NOME}}", escapeHtml(name))
                .replace("{{EXPIRA_EM}}", expireAt.format(EXPIRATION_FORMAT))
                .replace("{{ANO}}", String.valueOf(LocalDateTime.now().getYear()));
    }

    private static String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
