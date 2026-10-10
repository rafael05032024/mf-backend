package com.fmarket.provider;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmarket.exception.EmailException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ResendEmailProvider implements EmailProvider {

    private static final String EMAILS_PATH = "/emails";

    @ConfigProperty(name = "resend.base-url")
    String baseUrl;

    @ConfigProperty(name = "resend.api-key")
    String apiKey;

    @ConfigProperty(name = "resend.from")
    String from;

    @Inject
    ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public void sendEmail(String to, String subject, String html) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("from", from);
        body.put("to", to);
        body.put("subject", subject);
        body.put("html", html);

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + EMAILS_PATH))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new EmailException(
                        "Falha ao enviar e-mail no provedor de e-mail (status " + response.statusCode() + ")");
            }
        } catch (JsonProcessingException e) {
            throw new EmailException("Erro ao montar requisição do provedor de e-mail", e);
        } catch (IOException e) {
            throw new EmailException("Erro de comunicação com o provedor de e-mail", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EmailException("Requisição ao provedor de e-mail interrompida", e);
        }
    }
}
