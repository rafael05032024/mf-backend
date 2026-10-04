package com.fmarket.dto;

public record CreatePostResponseDTO(Long id, String type, String content, boolean isPrivate, String description) {
}
