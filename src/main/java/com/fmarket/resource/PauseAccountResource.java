package com.fmarket.resource;

import com.fmarket.security.Authenticated;
import com.fmarket.service.PauseAccountService;

import jakarta.inject.Inject;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/accounts/me/pause")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
public class PauseAccountResource {

    @Inject
    PauseAccountService pauseAccountService;

    @PATCH
    public Response pause(@Context SecurityContext security) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        pauseAccountService.pause(userId);
        return Response.noContent().build();
    }
}
