package com.fmarket.resource;

import com.fmarket.dto.CreateSignatureRequestDTO;
import com.fmarket.dto.CreateSignatureResponseDTO;
import com.fmarket.security.Authenticated;
import com.fmarket.service.CreateSignatureService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/signatures")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CreateSignatureResource {

    @Inject
    CreateSignatureService createSignatureService;

    @POST
    public CreateSignatureResponseDTO create(@Context SecurityContext security,
            @Valid CreateSignatureRequestDTO request) {
        Long subscriberId = Long.valueOf(security.getUserPrincipal().getName());
        return createSignatureService.create(subscriberId, request);
    }
}
