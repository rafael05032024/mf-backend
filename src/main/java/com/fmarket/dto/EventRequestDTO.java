package com.fmarket.dto;

import jakarta.validation.constraints.NotBlank;

public record EventRequestDTO(@NotBlank String type, Object data) {
}
