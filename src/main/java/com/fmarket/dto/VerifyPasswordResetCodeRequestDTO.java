package com.fmarket.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyPasswordResetCodeRequestDTO(
        @NotBlank @Email String email,
        @NotBlank String code) {
}
