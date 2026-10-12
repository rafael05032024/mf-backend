package com.fmarket.resource;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fmarket.dto.SendMessageRequestDTO;
import com.fmarket.dto.SendMessageResponseDTO;
import com.fmarket.exception.ActionForbiddenException;
import com.fmarket.security.JwtTokenReader;
import com.fmarket.service.SendMessageService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

@Path("/api/messages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SendMessageResource {

    @Inject
    SendMessageService sendMessageService;

    @Inject
    JwtTokenReader tokenReader;

    @ConfigProperty(name = "app.owner-id")
    String ownerId;

    @POST
    public SendMessageResponseDTO send(
            @HeaderParam("x-owner-id") String ownerHeader,
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid SendMessageRequestDTO request) {
        return sendMessageService.send(resolveFrom(ownerHeader, authorization, request), request);
    }

    private Long resolveFrom(String ownerHeader, String authorization, SendMessageRequestDTO request) {
        if (ownerHeader == null) {
            // Sem x-owner-id: exige token (lança "Token ausente" se não houver).
            return tokenReader.readUserId(authorization);
        }

        if (!isOwner(ownerHeader)) {
            throw new ActionForbiddenException("Ação não pode ser realizada");
        }

        // Dono + token: remetente é o usuário logado. Dono sem token: vale o from do body (pode ser nulo).
        if (authorization != null && !authorization.isBlank()) {
            return tokenReader.readUserId(authorization);
        }
        return request.from();
    }

    private boolean isOwner(String value) {
        return MessageDigest.isEqual(value.getBytes(StandardCharsets.UTF_8),
                ownerId.getBytes(StandardCharsets.UTF_8));
    }
}
