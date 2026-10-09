package com.fmarket.resource;

import com.fmarket.dto.GetMeResponseDTO;
import com.fmarket.security.Authenticated;
import com.fmarket.service.GetMeService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/accounts/me")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
public class GetMeResource {

    @Inject
    GetMeService getMeService;

    @GET
    public GetMeResponseDTO get(@Context SecurityContext security) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        return getMeService.get(userId);
    }
}
