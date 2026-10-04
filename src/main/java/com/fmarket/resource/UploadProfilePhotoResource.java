package com.fmarket.resource;

import com.fmarket.dto.UploadProfilePhotoRequestDTO;
import com.fmarket.exception.InvalidImageException;
import com.fmarket.security.Authenticated;
import com.fmarket.service.UploadProfilePhotoService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.nio.file.Files;

@Path("/api/accounts/me/photo")
@Authenticated
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class UploadProfilePhotoResource {

    @Inject
    UploadProfilePhotoService uploadProfilePhotoService;

    @POST
    public void upload(@Context SecurityContext security, @RestForm("file") FileUpload file) {
        if (file == null) {
            throw new InvalidImageException("Nenhuma imagem enviada");
        }
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        try {
            byte[] content = Files.readAllBytes(file.uploadedFile());
            uploadProfilePhotoService.upload(userId,
                    new UploadProfilePhotoRequestDTO(file.contentType(), content));
        } catch (IOException e) {
            throw new InvalidImageException("Não foi possível ler a imagem enviada");
        }
    }
}
