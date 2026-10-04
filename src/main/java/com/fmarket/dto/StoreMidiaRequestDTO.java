package com.fmarket.dto;

import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoreMidiaRequestDTO(
        @NotBlank FileUpload file,
        @NotNull String midiaName) {
}
