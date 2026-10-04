package com.fmarket.resource;

import java.io.IOException;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import com.fmarket.dto.UploadCoverPhotoRequestDTO;
import com.fmarket.exception.InvalidImageException;
import com.fmarket.security.Authenticated;
import com.fmarket.service.UploadCoverPhotoService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/accounts/me/cover")
@Authenticated
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class UploadCoverPhotoResource {

    @Inject
    UploadCoverPhotoService uploadCoverPhotoService;

    @POST
    public void upload(@Context SecurityContext security, @RestForm("file") FileUpload file) {
        if (file == null) {
            throw new InvalidImageException("Nenhuma imagem enviada");
        }
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        try {
            uploadCoverPhotoService.upload(userId,
                    new UploadCoverPhotoRequestDTO(file));
        } catch (IOException e) {
            throw new InvalidImageException("Não foi possível ler a imagem enviada");
        }
    }
}
