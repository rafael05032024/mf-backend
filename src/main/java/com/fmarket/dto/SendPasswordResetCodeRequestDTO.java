package com.fmarket.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendPasswordResetCodeRequestDTO(
        @NotBlank @Email String email) {
}
