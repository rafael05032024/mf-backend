package com.fmarket.provider;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmarket.dto.GeneratePixQrCodeRequestDTO;
import com.fmarket.dto.GeneratePixQrCodeResponseDTO;
import com.fmarket.exception.PaymentGatewayException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AsaasPaymentGatewayProvider implements PaymentGatewayProvider {

    private static final String STATIC_QR_CODE_PATH = "/v3/pix/qrCodes/static";
    private static final DateTimeFormatter EXPIRATION_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ConfigProperty(name = "asaas.base-url")
    String baseUrl;

    @ConfigProperty(name = "asaas.access-token")
    String accessToken;

    @Inject
    ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public GeneratePixQrCodeResponseDTO generatePixQrCode(GeneratePixQrCodeRequestDTO request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("addressKey", request.destination());
        body.put("description", request.description());
        body.put("value", request.value());
        body.put("format", "ALL");
        body.put("expirationDate", request.expirationDate().format(EXPIRATION_FORMAT));

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + STATIC_QR_CODE_PATH))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .header("access_token", accessToken)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new PaymentGatewayException(
                        "Falha ao gerar QR Code Pix no gateway de pagamento (status " + response.statusCode() + ")");
            }

            JsonNode encodedImage = objectMapper.readTree(response.body()).get("encodedImage");
            if (encodedImage == null || encodedImage.isNull()) {
                throw new PaymentGatewayException("Resposta do gateway de pagamento sem imagem do QR Code");
            }

            return new GeneratePixQrCodeResponseDTO(encodedImage.asText());
        } catch (JsonProcessingException e) {
            throw new PaymentGatewayException("Resposta inválida do gateway de pagamento", e);
        } catch (IOException e) {
            throw new PaymentGatewayException("Erro de comunicação com o gateway de pagamento", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PaymentGatewayException("Requisição ao gateway de pagamento interrompida", e);
        }
    }
}
