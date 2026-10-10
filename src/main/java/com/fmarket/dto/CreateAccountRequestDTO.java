package com.fmarket.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateAccountRequestDTO(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String profile,
        @NotBlank String password,
        @NotBlank String code) {
}
