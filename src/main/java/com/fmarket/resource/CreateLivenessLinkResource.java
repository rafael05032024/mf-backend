package com.fmarket.resource;

import com.fmarket.dto.CreateLivenessLinkResponseDTO;
import com.fmarket.security.Authenticated;
import com.fmarket.service.CreateLivenessLinkService;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/liveness")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
public class CreateLivenessLinkResource {

    @Inject
    CreateLivenessLinkService createLivenessLinkService;

    @POST
    public CreateLivenessLinkResponseDTO create(@Context SecurityContext security) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        return createLivenessLinkService.create(userId);
    }
}
