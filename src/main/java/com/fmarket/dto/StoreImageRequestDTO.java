package com.fmarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoreImageRequestDTO(
        @NotBlank String fileName,
        @NotBlank String contentType,
        @NotNull byte[] content) {
}
