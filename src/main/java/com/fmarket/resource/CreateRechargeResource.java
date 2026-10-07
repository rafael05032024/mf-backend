package com.fmarket.resource;

import com.fmarket.dto.CreateRechargeRequestDTO;
import com.fmarket.dto.CreateRechargeResponseDTO;
import com.fmarket.security.Authenticated;
import com.fmarket.service.CreateRechargeService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/wallet/recharges")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CreateRechargeResource {

    @Inject
    CreateRechargeService createRechargeService;

    @POST
    public CreateRechargeResponseDTO create(@Context SecurityContext security,
            @Valid CreateRechargeRequestDTO request) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        return createRechargeService.create(userId, request);
    }
}
