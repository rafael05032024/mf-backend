package com.fmarket.resource;

import com.fmarket.dto.CreatePlanRequestDTO;
import com.fmarket.dto.CreatePlanResponseDTO;
import com.fmarket.security.Authenticated;
import com.fmarket.service.CreatePlanService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/plans")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CreatePlanResource {

    @Inject
    CreatePlanService createPlanService;

    @POST
    public CreatePlanResponseDTO create(@Context SecurityContext security, @Valid CreatePlanRequestDTO request) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        return createPlanService.create(userId, request);
    }
}
