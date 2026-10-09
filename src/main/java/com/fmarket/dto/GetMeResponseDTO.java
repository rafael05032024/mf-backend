package com.fmarket.dto;

import java.math.BigDecimal;

public record GetMeResponseDTO(String name, String profile, String thumb, BigDecimal balance, long subscriptions,
        Boolean verified) {
}
