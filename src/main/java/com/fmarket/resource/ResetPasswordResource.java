package com.fmarket.resource;

import com.fmarket.dto.ResetPasswordRequestDTO;
import com.fmarket.exception.ActionForbiddenException;
import com.fmarket.security.JwtTokenReader;
import com.fmarket.service.ResetPasswordService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/accounts/password-reset")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ResetPasswordResource {

    @Inject
    ResetPasswordService resetPasswordService;

    @Inject
    JwtTokenReader tokenReader;

    @PUT
    public Response reset(
            @HeaderParam("x-session-id") String sessionId,
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid ResetPasswordRequestDTO request) {
        if (sessionId != null && !sessionId.isBlank()) {
            resetPasswordService.resetBySession(sessionId, request.password());
        } else if (authorization != null && !authorization.isBlank()) {
            resetPasswordService.resetByUser(tokenReader.readUserId(authorization), request.password());
        } else {
            throw new ActionForbiddenException("Ação não pode ser realizada");
        }
        return Response.noContent().build();
    }
}
