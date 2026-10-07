package com.fmarket.resource;

import java.util.List;

import com.fmarket.dto.ListSignedProfilesResponseDTO;
import com.fmarket.security.Authenticated;
import com.fmarket.service.ListSignedProfilesService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/signatures")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
public class ListSignedProfilesResource {

    @Inject
    ListSignedProfilesService listSignedProfilesService;

    @GET
    public List<ListSignedProfilesResponseDTO> list(@Context SecurityContext security) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        return listSignedProfilesService.list(userId);
    }
}
