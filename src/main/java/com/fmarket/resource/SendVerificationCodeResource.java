package com.fmarket.resource;

import com.fmarket.dto.SendVerificationCodeRequestDTO;
import com.fmarket.service.SendVerificationCodeService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/accounts/verification-code")
@Consumes(MediaType.APPLICATION_JSON)
public class SendVerificationCodeResource {

    @Inject
    SendVerificationCodeService sendVerificationCodeService;

    @POST
    public Response send(@Valid SendVerificationCodeRequestDTO request) {
        sendVerificationCodeService.send(request);
        return Response.noContent().build();
    }
}
