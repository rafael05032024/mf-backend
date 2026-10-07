package com.fmarket.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GeneratePixQrCodeRequestDTO(
        BigDecimal value,
        String description,
        LocalDateTime expirationDate) {
}
