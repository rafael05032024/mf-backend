package com.fmarket.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.fmarket.dto.StoreMidiaRequestDTO;
import com.fmarket.dto.UploadCoverPhotoRequestDTO;
import com.fmarket.exception.InvalidImageException;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.UserModel;
import com.fmarket.provider.BlobStorageProvider;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UploadCoverPhotoService {

    private static final long MAX_SIZE_BYTES = 5L * 1024 * 1024;
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp");

    @Inject
    UserRepository userRepository;

    @Inject
    BlobStorageProvider blobStorageProvider;

    @Transactional
    public void upload(Long userId, UploadCoverPhotoRequestDTO request) throws IOException {
        if (request.file() == null) {
            throw new InvalidImageException("Nenhuma imagem enviada");
        }

        if (request.file().uploadedFile() == null || request.file().size() == 0) {
            throw new InvalidImageException("Nenhuma imagem enviada");
        }

        if (request.file().size() > MAX_SIZE_BYTES) {
            throw new InvalidImageException("A imagem deve ter no máximo 5 MB");
        }

        String contentType = request.file().contentType() == null ? "" : request.file().contentType().toLowerCase();
        String extension = EXTENSIONS.get(contentType);

        if (extension == null) {
            throw new InvalidImageException("Formato inválido. Use JPEG, PNG ou WEBP");
        }

        UserModel user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        String fileName = UUID.randomUUID() + "." + extension;

        blobStorageProvider.store(new StoreMidiaRequestDTO(request.file(), fileName));

        user.coverPhoto = fileName;
        user.updatedAt = LocalDateTime.now();
    }
}
