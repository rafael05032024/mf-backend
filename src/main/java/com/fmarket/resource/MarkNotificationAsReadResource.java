package com.fmarket.resource;

import com.fmarket.security.Authenticated;
import com.fmarket.service.MarkNotificationAsReadService;

import jakarta.inject.Inject;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/notifications/{id}/read")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
public class MarkNotificationAsReadResource {

    @Inject
    MarkNotificationAsReadService markNotificationAsReadService;

    @PATCH
    public Response markAsRead(@PathParam("id") Long id, @Context SecurityContext security) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        markNotificationAsReadService.markAsRead(userId, id);
        return Response.noContent().build();
    }
}
