package com.fmarket.resource;

import com.fmarket.security.Authenticated;
import com.fmarket.service.DeleteAccountService;

import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/accounts/me")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
public class DeleteAccountResource {

    @Inject
    DeleteAccountService deleteAccountService;

    @DELETE
    public Response delete(@Context SecurityContext security) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        deleteAccountService.delete(userId);
        return Response.noContent().build();
    }
}
