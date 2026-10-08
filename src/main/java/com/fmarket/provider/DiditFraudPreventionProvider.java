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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmarket.dto.FraudPreventionExpectedDetailsDTO;
import com.fmarket.dto.FraudPreventionRequestDTO;
import com.fmarket.dto.FraudPreventionResponseDTO;
import com.fmarket.exception.FraudPreventionException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DiditFraudPreventionProvider implements FraudPreventionProvider {

    private static final String SESSION_PATH = "/v3/session/";
    private static final String LANGUAGE = "pt-BR";
    private static final String COUNTRY = "BRA";

    @ConfigProperty(name = "didit.base-url")
    String baseUrl;

    @ConfigProperty(name = "didit.api-key")
    String apiKey;

    @ConfigProperty(name = "didit.workflow-id")
    String workflowId;

    @Inject
    ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public FraudPreventionResponseDTO createVerificationSession(
            FraudPreventionRequestDTO request,
            FraudPreventionExpectedDetailsDTO expectedDetails) {
        Map<String, Object> contactDetails = new LinkedHashMap<>();
        contactDetails.put("email", request.userEmail());
        contactDetails.put("send_notification_emails", true);
        contactDetails.put("email_lang", LANGUAGE);

        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("first_name", expectedDetails.firstName());
        expected.put("last_name", expectedDetails.lastName());
        expected.put("date_of_birth", expectedDetails.dateOfBirth());
        expected.put("identification_number", expectedDetails.document());
        expected.put("id_country", COUNTRY);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workflow_id", workflowId);
        body.put("vendor_data", "user-" + request.userId());
        body.put("language", LANGUAGE);
        body.put("contact_details", contactDetails);
        body.put("expected_details", expected);

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + SESSION_PATH))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new FraudPreventionException(
                        "Falha ao criar sessão de verificação no provedor antifraude (status "
                                + response.statusCode() + ")");
            }

            JsonNode json = objectMapper.readTree(response.body());
            JsonNode sessionId = json.get("session_id");
            if (sessionId == null || sessionId.isNull()) {
                throw new FraudPreventionException("Resposta do provedor antifraude sem id da sessão");
            }

            JsonNode url = json.get("url");
            if (url == null || url.isNull()) {
                throw new FraudPreventionException("Resposta do provedor antifraude sem url de verificação");
            }

            return new FraudPreventionResponseDTO(sessionId.asText(), url.asText());
        } catch (JsonProcessingException e) {
            throw new FraudPreventionException("Resposta inválida do provedor antifraude", e);
        } catch (IOException e) {
            throw new FraudPreventionException("Erro de comunicação com o provedor antifraude", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FraudPreventionException("Requisição ao provedor antifraude interrompida", e);
        }
    }
}
