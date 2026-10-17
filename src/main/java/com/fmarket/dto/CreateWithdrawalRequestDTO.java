package com.fmarket.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateWithdrawalRequestDTO(
        @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal value,
        @NotBlank @Size(max = 255) @JsonProperty("address_key") String addressKey) {
}
