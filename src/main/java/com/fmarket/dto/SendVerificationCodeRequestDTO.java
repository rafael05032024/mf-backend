package com.fmarket.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendVerificationCodeRequestDTO(
        @NotBlank String name,
        @NotBlank @Email String email) {
}
