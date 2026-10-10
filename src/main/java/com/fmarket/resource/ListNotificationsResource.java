package com.fmarket.resource;

import com.fmarket.dto.ListNotificationsResponseDTO;
import com.fmarket.security.Authenticated;
import com.fmarket.service.ListNotificationsService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/notifications")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
public class ListNotificationsResource {

    @Inject
    ListNotificationsService listNotificationsService;

    @GET
    public ListNotificationsResponseDTO list(@Context SecurityContext security) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        return listNotificationsService.list(userId);
    }
}
