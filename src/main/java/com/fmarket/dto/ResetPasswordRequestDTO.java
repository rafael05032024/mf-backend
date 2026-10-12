package com.fmarket.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequestDTO(
        @NotBlank @StrongPassword String password) {
}
