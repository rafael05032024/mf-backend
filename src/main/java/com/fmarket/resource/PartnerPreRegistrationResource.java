package com.fmarket.resource;

import com.fmarket.security.Authenticated;
import com.fmarket.service.PartnerPreRegistrationService;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/partners/pre-registration")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
public class PartnerPreRegistrationResource {

    @Inject
    PartnerPreRegistrationService partnerPreRegistrationService;

    @POST
    public Response register(@Context SecurityContext security) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        partnerPreRegistrationService.register(userId);
        return Response.noContent().build();
    }
}
