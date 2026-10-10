package com.fmarket.resource;

import com.fmarket.dto.UpdatePlanRequestDTO;
import com.fmarket.security.Authenticated;
import com.fmarket.service.UpdatePlanService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/plans")
@Authenticated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UpdatePlanResource {

    @Inject
    UpdatePlanService updatePlanService;

    @PUT
    public Response update(
            @Context SecurityContext security,
            @Valid UpdatePlanRequestDTO request) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        updatePlanService.update(userId, request);
        return Response.noContent().build();
    }
}
