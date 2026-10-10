package com.fmarket.service;

import java.util.Map;
import java.util.UUID;

import org.jboss.resteasy.reactive.multipart.FileUpload;

import com.fmarket.exception.InvalidImageException;
import com.fmarket.model.PostType;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostMidiaValidator {

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

    public record ValidatedMidia(PostType type, String fileName) {
    }

    public ValidatedMidia validate(FileUpload midia) {
        if (midia == null || midia.uploadedFile() == null || midia.size() == 0) {
            throw new InvalidImageException("Nenhuma mídia enviada");
        }

        String contentType = midia.contentType() == null ? "" : midia.contentType().toLowerCase();
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

        if (midia.size() > maxSize) {
            throw new InvalidImageException(
                    "A mídia deve ter no máximo " + (maxSize / (1024 * 1024)) + " MB");
        }

        return new ValidatedMidia(type, UUID.randomUUID() + "." + extension);
    }

    public boolean parseIsPrivate(String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new InvalidImageException("is_private deve ser true ou false");
    }

    public String parseDescription(String value) {
        String description = value == null ? "" : value.trim();
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new InvalidImageException("A descrição deve ter no máximo " + MAX_DESCRIPTION_LENGTH + " caracteres");
        }
        return description;
    }
}
