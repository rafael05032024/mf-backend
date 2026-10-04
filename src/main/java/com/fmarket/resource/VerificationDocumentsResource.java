package com.fmarket.resource;

import com.fmarket.dto.ImageFileDTO;
import com.fmarket.dto.VerificationDocumentsRequestDTO;
import com.fmarket.exception.InvalidImageException;
import com.fmarket.security.Authenticated;
import com.fmarket.service.VerificationDocumentsService;

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

@Path("/api/accounts/me/verification")
@Authenticated
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class VerificationDocumentsResource {

    @Inject
    VerificationDocumentsService verificationDocumentsService;

    @POST
    public void upload(
            @Context SecurityContext security,
            @RestForm("rg_front") FileUpload rgFront,
            @RestForm("rg_back") FileUpload rgBack,
            @RestForm("selfie_with_rg") FileUpload selfieWithRg) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        verificationDocumentsService.upload(userId, new VerificationDocumentsRequestDTO(
                toDto(rgFront), toDto(rgBack), toDto(selfieWithRg)));
    }

    private ImageFileDTO toDto(FileUpload file) {
        if (file == null) {
            return null;
        }
        try {
            return new ImageFileDTO(file.contentType(), Files.readAllBytes(file.uploadedFile()));
        } catch (IOException e) {
            throw new InvalidImageException("Não foi possível ler a imagem enviada");
        }
    }
}
