package com.fmarket.service;

import java.util.Map;
import java.util.UUID;

import com.fmarket.dto.ImageFileDTO;
import com.fmarket.dto.VerificationDocumentsRequestDTO;
import com.fmarket.exception.InvalidImageException;
import com.fmarket.provider.BlobStorageProvider;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VerificationDocumentsService {

    private static final long MAX_SIZE_BYTES = 5L * 1024 * 1024;
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp");

    @Inject
    BlobStorageProvider blobStorageProvider;

    public void upload(Long userId, VerificationDocumentsRequestDTO request) {
        // Valida as três antes de enviar qualquer uma, para não deixar upload parcial.
        String frontExt = validate(request.rgFront(), "Frente do RG");
        String backExt = validate(request.rgBack(), "Verso do RG");
        String selfieExt = validate(request.selfieWithRg(), "Foto segurando o RG");

        String folder = "verification/" + userId + "/" + UUID.randomUUID() + "/";
        store(folder + "rg-front." + frontExt, request.rgFront());
        store(folder + "rg-back." + backExt, request.rgBack());
        store(folder + "selfie-with-rg." + selfieExt, request.selfieWithRg());
    }

    private String validate(ImageFileDTO image, String label) {
        if (image == null || image.content() == null || image.content().length == 0) {
            throw new InvalidImageException(label + ": nenhuma imagem enviada");
        }
        if (image.content().length > MAX_SIZE_BYTES) {
            throw new InvalidImageException(label + ": a imagem deve ter no máximo 5 MB");
        }
        String extension = EXTENSIONS.get(image.contentType() == null ? "" : image.contentType().toLowerCase());
        if (extension == null) {
            throw new InvalidImageException(label + ": formato inválido. Use JPEG, PNG ou WEBP");
        }
        return extension;
    }

    private void store(String fileName, ImageFileDTO image) {
        return;
    }
}
