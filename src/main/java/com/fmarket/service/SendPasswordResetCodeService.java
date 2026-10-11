package com.fmarket.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import com.fmarket.dto.SendPasswordResetCodeRequestDTO;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.exception.VerificationCodeLimitException;
import com.fmarket.model.ResetPassRequestModel;
import com.fmarket.provider.EmailProvider;
import com.fmarket.repository.ResetPassRequestRepository;
import com.fmarket.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class SendPasswordResetCodeService {

    private static final String TEMPLATE_PATH = "/templates/password-reset-code-email.html";
    private static final String SUBJECT = "Recuperação de senha – My Foot";
    private static final int CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 10;
    private static final int MAX_REQUESTS_PER_EMAIL = 10;
    private static final int LIMIT_WINDOW_DAYS = 3;
    private static final String EXPIRATION_TEXT = EXPIRATION_MINUTES + " minutos";

    private static final Logger LOG = Logger.getLogger(SendPasswordResetCodeService.class);

    private final SecureRandom random = new SecureRandom();

    private String template;

    @Inject
    UserRepository userRepository;

    @Inject
    ResetPassRequestRepository resetPassRequestRepository;

    @Inject
    EmailProvider emailProvider;

    @ConfigProperty(name = "email.send.enabled")
    boolean emailSendEnabled;

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
    public void send(SendPasswordResetCodeRequestDTO request) {
        String email = request.email().trim();

        userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Nenhum cadastro encontrado para o e-mail informado"));

        if (resetPassRequestRepository.countByEmailSince(email,
                LocalDateTime.now().minusDays(LIMIT_WINDOW_DAYS)) >= MAX_REQUESTS_PER_EMAIL) {
            throw new VerificationCodeLimitException("Ação não mais permitida: limite de solicitações atingido");
        }

        String code = generateCode();

        ResetPassRequestModel resetPass = new ResetPassRequestModel();
        resetPass.email = email;
        resetPass.code = code;
        resetPass.expireAt = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        resetPassRequestRepository.persist(resetPass);

        if (!emailSendEnabled) {
            LOG.infof("Envio de e-mail desabilitado (email.send.enabled=false). Código de redefinição para %s: %s",
                    email, code);
            return;
        }

        emailProvider.sendEmail(email, SUBJECT, render(code));
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    private String render(String code) {
        String html = template;
        for (int i = 0; i < code.length(); i++) {
            html = html.replace("{{CODIGO_" + (i + 1) + "}}", String.valueOf(code.charAt(i)));
        }
        return html
                .replace("{{CODIGO}}", code)
                .replace("{{EXPIRA_EM}}", EXPIRATION_TEXT)
                .replace("{{ANO}}", String.valueOf(LocalDateTime.now().getYear()));
    }
}
