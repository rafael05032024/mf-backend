package com.fmarket.resource;

import com.fmarket.dto.CreateWithdrawalRequestDTO;
import com.fmarket.security.Authenticated;
import com.fmarket.service.CreateWithdrawalRequestService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/wallet/withdrawals")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
public class CreateWithdrawalRequestResource {

    @Inject
    CreateWithdrawalRequestService createWithdrawalRequestService;

    @POST
    public Response create(@Context SecurityContext security, @Valid CreateWithdrawalRequestDTO request) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        createWithdrawalRequestService.create(userId, request);
        return Response.noContent().build();
    }
}
