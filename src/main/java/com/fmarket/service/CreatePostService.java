package com.fmarket.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.fmarket.dto.CreatePostRequestDTO;
import com.fmarket.dto.CreatePostResponseDTO;
import com.fmarket.dto.StoreMidiaRequestDTO;
import com.fmarket.exception.InvalidImageException;
import com.fmarket.exception.UnverifiedPublisherException;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.PostType;
import com.fmarket.model.UserModel;
import com.fmarket.model.UserPost;
import com.fmarket.provider.BlobStorageProvider;
import com.fmarket.repository.UserPostRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreatePostService {

    private static final long MAX_IMAGE_SIZE_BYTES = 5L * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE_BYTES = 20L * 1024 * 1024;
    private static final int MAX_DESCRIPTION_LENGTH = 255;

    private static final Map<String, String> IMAGE_EXTENSIONS = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp");
    private static final Map<String, String> VIDEO_EXTENSIONS = Map.of(
            "video/mp4", "mp4",
            "video/webm", "webm",
            "video/quicktime", "mov");

    @Inject
    UserRepository userRepository;

    @Inject
    UserPostRepository userPostRepository;

    @Inject
    BlobStorageProvider blobStorageProvider;

    @Transactional
    public CreatePostResponseDTO create(Long userId, CreatePostRequestDTO request) throws IOException {
        if (request.midia() == null || request.midia().uploadedFile() == null || request.midia().size() == 0) {
            throw new InvalidImageException("Nenhuma mídia enviada");
        }

        boolean isPrivate = parseIsPrivate(request.isPrivate());

        String description = request.description() == null ? "" : request.description().trim();
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new InvalidImageException("A descrição deve ter no máximo " + MAX_DESCRIPTION_LENGTH + " caracteres");
        }

        String contentType = request.midia().contentType() == null ? "" : request.midia().contentType().toLowerCase();
        PostType type;
        String extension;
        long maxSize;

        if (IMAGE_EXTENSIONS.containsKey(contentType)) {
            type = PostType.IMAGE;
            extension = IMAGE_EXTENSIONS.get(contentType);
            maxSize = MAX_IMAGE_SIZE_BYTES;
        } else if (VIDEO_EXTENSIONS.containsKey(contentType)) {
            type = PostType.VIDEO;
            extension = VIDEO_EXTENSIONS.get(contentType);
            maxSize = MAX_VIDEO_SIZE_BYTES;
        } else {
            throw new InvalidImageException("Formato inválido. Use JPEG, PNG, WEBP, MP4, WEBM ou MOV");
        }

        if (request.midia().size() > maxSize) {
            throw new InvalidImageException(
                    "A mídia deve ter no máximo " + (maxSize / (1024 * 1024)) + " MB");
        }

        UserModel user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        if (!Boolean.TRUE.equals(user.verified)) {
            throw new UnverifiedPublisherException(
                    "Você não pode postar conteúdo porque não possui uma conta de publicador verificada");
        }

        String fileName = UUID.randomUUID() + "." + extension;

        blobStorageProvider.store(new StoreMidiaRequestDTO(request.midia(), fileName));

        UserPost post = new UserPost();
        post.owner = userId;
        post.type = type;
        post.content = fileName;
        post.isPrivate = isPrivate;
        post.description = description;
        post.createdAt = LocalDateTime.now();
        post.updatedAt = post.createdAt;
        userPostRepository.persist(post);

        return new CreatePostResponseDTO(post.id, type.name(), fileName, isPrivate, description);
    }

    private boolean parseIsPrivate(String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new InvalidImageException("is_private deve ser true ou false");
    }
}
