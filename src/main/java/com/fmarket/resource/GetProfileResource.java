package com.fmarket.resource;

import com.fmarket.dto.GetProfileResponseDTO;
import com.fmarket.service.GetProfileService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/profiles/{profile}")
@Produces(MediaType.APPLICATION_JSON)
public class GetProfileResource {

    @Inject
    GetProfileService getProfileService;

    @GET
    public GetProfileResponseDTO get(@PathParam("profile") String profile) {
        return getProfileService.get(profile);
    }
}
