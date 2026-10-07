package com.fmarket.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record CreateRechargeRequestDTO(@NotNull BigDecimal value) {
}
