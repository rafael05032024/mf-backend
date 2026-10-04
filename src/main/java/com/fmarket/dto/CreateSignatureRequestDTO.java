package com.fmarket.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSignatureRequestDTO(@NotBlank String producer) {
}
