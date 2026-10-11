package com.fmarket.resource;

import com.fmarket.dto.SendPasswordResetCodeRequestDTO;
import com.fmarket.service.SendPasswordResetCodeService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/accounts/password-reset/code")
@Consumes(MediaType.APPLICATION_JSON)
public class SendPasswordResetCodeResource {

    @Inject
    SendPasswordResetCodeService sendPasswordResetCodeService;

    @POST
    public Response send(@Valid SendPasswordResetCodeRequestDTO request) {
        sendPasswordResetCodeService.send(request);
        return Response.noContent().build();
    }
}
