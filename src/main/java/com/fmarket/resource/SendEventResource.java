package com.fmarket.resource;

import com.fmarket.dto.EventRequestDTO;
import com.fmarket.dto.EventResponseDTO;
import com.fmarket.dto.EventSentResponseDTO;
import com.fmarket.service.EventService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/** Envia um evento para as conexões WebSocket abertas de um usuário. */
@Path("/api/events/{id}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SendEventResource {

    @Inject
    EventService eventService;

    @POST
    public EventSentResponseDTO send(@PathParam("id") Long id, @Valid EventRequestDTO request) {
        int delivered = eventService.publish(id, new EventResponseDTO(request.type(), request.data()));
        return new EventSentResponseDTO(delivered);
    }
}
