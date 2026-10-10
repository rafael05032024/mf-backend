package com.fmarket.resource;

import com.fmarket.security.Authenticated;
import com.fmarket.service.DeletePostService;

import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/posts/{id}")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
public class DeletePostResource {

    @Inject
    DeletePostService deletePostService;

    @DELETE
    public Response delete(@PathParam("id") Long id, @Context SecurityContext security) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        deletePostService.delete(userId, id);
        return Response.noContent().build();
    }
}
