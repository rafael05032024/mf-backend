package com.fmarket.resource;

import com.fmarket.security.Authenticated;
import com.fmarket.service.MarkMessagesAsReadService;

import jakarta.inject.Inject;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/messages/read")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
public class MarkMessagesAsReadResource {

    @Inject
    MarkMessagesAsReadService markMessagesAsReadService;

    @PATCH
    public Response markAllAsRead(@Context SecurityContext security) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        markMessagesAsReadService.markAllAsRead(userId);
        return Response.noContent().build();
    }
}
