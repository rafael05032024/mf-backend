package com.fmarket.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/status")
public class StatusResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StatusResponse status() {
        return new StatusResponse("UP", "fmarket-java");
    }

    public record StatusResponse(String status, String service) {
    }
}
