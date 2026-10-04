package com.fmarket.resource;

import java.io.IOException;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import com.fmarket.dto.CreatePostRequestDTO;
import com.fmarket.exception.InvalidImageException;
import com.fmarket.security.Authenticated;
import com.fmarket.service.CreatePostService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/posts")
@Authenticated
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class CreatePostResource {

    @Inject
    CreatePostService createPostService;

    @POST
    public Response create(
            @Context SecurityContext security,
            @RestForm("midia") FileUpload midia,
            @RestForm("is_private") String isPrivate,
            @RestForm("description") String description) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        try {
            createPostService.create(userId, new CreatePostRequestDTO(midia, isPrivate, description));
            return Response.noContent().build();
        } catch (IOException e) {
            throw new InvalidImageException("Não foi possível ler a mídia enviada");
        }
    }
}
