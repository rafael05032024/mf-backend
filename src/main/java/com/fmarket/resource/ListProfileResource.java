package com.fmarket.resource;

import com.fmarket.dto.ListProfileResponseDTO;
import com.fmarket.service.ListProfileService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/api/profiles")
@Produces(MediaType.APPLICATION_JSON)
public class ListProfileResource {

    @Inject
    ListProfileService listProfileService;

    @GET
    public List<ListProfileResponseDTO> list(@QueryParam("q") String q) {
        return listProfileService.list(q);
    }
}
