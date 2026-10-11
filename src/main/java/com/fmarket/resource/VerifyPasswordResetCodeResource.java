package com.fmarket.resource;

import com.fmarket.dto.VerifyPasswordResetCodeRequestDTO;
import com.fmarket.dto.VerifyPasswordResetCodeResponseDTO;
import com.fmarket.service.VerifyPasswordResetCodeService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/accounts/password-reset/verify-code")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VerifyPasswordResetCodeResource {

    @Inject
    VerifyPasswordResetCodeService verifyPasswordResetCodeService;

    @POST
    public VerifyPasswordResetCodeResponseDTO verify(@Valid VerifyPasswordResetCodeRequestDTO request) {
        return verifyPasswordResetCodeService.verify(request);
    }
}
