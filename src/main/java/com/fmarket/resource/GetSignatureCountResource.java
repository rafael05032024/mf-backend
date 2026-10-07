package com.fmarket.resource;

import com.fmarket.dto.GetSignatureCountResponseDTO;
import com.fmarket.security.Authenticated;
import com.fmarket.service.GetSignatureCountService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/signatures/count")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
public class GetSignatureCountResource {

    @Inject
    GetSignatureCountService getSignatureCountService;

    @GET
    public GetSignatureCountResponseDTO get(@Context SecurityContext security) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        return getSignatureCountService.get(userId);
    }
}
