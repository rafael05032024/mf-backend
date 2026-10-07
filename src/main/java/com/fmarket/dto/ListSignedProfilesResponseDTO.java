package com.fmarket.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ListSignedProfilesResponseDTO(
        String profile,
        String thumb,
        @JsonProperty("expire_at") LocalDateTime expireAt) {
}
