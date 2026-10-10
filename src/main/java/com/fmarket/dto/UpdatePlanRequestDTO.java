package com.fmarket.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record UpdatePlanRequestDTO(
        @NotNull @Digits(integer = 17, fraction = 2) BigDecimal value) {
}
