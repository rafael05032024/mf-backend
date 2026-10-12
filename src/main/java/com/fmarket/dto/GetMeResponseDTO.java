package com.fmarket.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetMeResponseDTO(Long id, String name, String profile, String thumb, BigDecimal balance, long subscriptions,
        Boolean verified, @JsonProperty("cover_photo") String coverPhoto,
        @JsonProperty("plan_value") BigDecimal planValue, Integer level) {
}
