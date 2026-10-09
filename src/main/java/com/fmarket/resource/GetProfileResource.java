package com.fmarket.resource;

import com.fmarket.dto.GetProfileResponseDTO;
import com.fmarket.security.JwtTokenReader;
import com.fmarket.service.GetProfileService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

@Path("/api/profiles/{profile}")
@Produces(MediaType.APPLICATION_JSON)
public class GetProfileResource {

    @Inject
    GetProfileService getProfileService;

    @Inject
    JwtTokenReader tokenReader;

    @GET
    public GetProfileResponseDTO get(@PathParam("profile") String profile,
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization) {
        // Autenticação opcional: sem header = anônimo; token presente porém inválido = 401.
        Long userId = authorization == null ? null : tokenReader.readUserId(authorization);

        return getProfileService.get(profile, userId);
    }
}
