package com.fmarket.resource;

import com.fmarket.dto.UpdateAccountRequestDTO;
import com.fmarket.security.Authenticated;
import com.fmarket.service.UpdateAccountService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/accounts/{id}")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UpdateAccountByIdResource {

    @Inject
    UpdateAccountService updateAccountService;

    @PATCH
    public Response update(@Context SecurityContext security, @PathParam("id") Long id, @Valid UpdateAccountRequestDTO request) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        updateAccountService.update(userId, id, request);
        return Response.noContent().build();
    }
}
