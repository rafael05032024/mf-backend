package com.fmarket.resource;

import com.fmarket.dto.ListMessagesResponseDTO;
import com.fmarket.security.Authenticated;
import com.fmarket.service.ListMessagesService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/messages")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
public class ListMessagesResource {

    @Inject
    ListMessagesService listMessagesService;

    @GET
    public ListMessagesResponseDTO list(@Context SecurityContext security) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        return listMessagesService.list(userId);
    }
}
