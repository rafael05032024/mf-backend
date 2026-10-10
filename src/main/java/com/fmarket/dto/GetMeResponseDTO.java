package com.fmarket.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetMeResponseDTO(String name, String profile, String thumb, BigDecimal balance, long subscriptions,
        Boolean verified, @JsonProperty("cover_photo") String coverPhoto) {
}
