package com.fmarket.service;

import com.fmarket.dto.StoreImageRequestDTO;
import com.fmarket.dto.UploadProfilePhotoRequestDTO;
import com.fmarket.exception.InvalidImageException;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.UserModel;
import com.fmarket.provider.BlobStorageProvider;
import com.fmarket.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class UploadProfilePhotoService {

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
    public void upload(Long userId, UploadProfilePhotoRequestDTO request) {
        if (request.content() == null || request.content().length == 0) {
            throw new InvalidImageException("Nenhuma imagem enviada");
        }
        if (request.content().length > MAX_SIZE_BYTES) {
            throw new InvalidImageException("A imagem deve ter no máximo 5 MB");
        }
        String contentType = request.contentType() == null ? "" : request.contentType().toLowerCase();
        String extension = EXTENSIONS.get(contentType);
        if (extension == null) {
            throw new InvalidImageException("Formato inválido. Use JPEG, PNG ou WEBP");
        }

        UserModel user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        String fileName = "profile/" + user.id + "/" + UUID.randomUUID() + "." + extension;
        String url = blobStorageProvider
                .storeImage(new StoreImageRequestDTO(fileName, contentType, request.content()))
                .url();

        user.thumb = url;
        user.updatedAt = LocalDateTime.now();
    }
}
