package com.fmarket.resource;

import java.io.IOException;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import com.fmarket.dto.UpdatePostRequestDTO;
import com.fmarket.exception.InvalidImageException;
import com.fmarket.security.Authenticated;
import com.fmarket.service.UpdatePostService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/posts/{id}")
@Authenticated
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class UpdatePostResource {

    @Inject
    UpdatePostService updatePostService;

    @PUT
    public Response update(
            @PathParam("id") Long id,
            @Context SecurityContext security,
            @RestForm("midia") FileUpload midia,
            @RestForm("is_private") String isPrivate,
            @RestForm("description") String description) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        try {
            updatePostService.update(userId, id, new UpdatePostRequestDTO(midia, isPrivate, description));
            return Response.noContent().build();
        } catch (IOException e) {
            throw new InvalidImageException("Não foi possível ler a mídia enviada");
        }
    }
}
