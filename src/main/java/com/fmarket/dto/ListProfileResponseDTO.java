package com.fmarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ListProfileResponseDTO(
        String profile,
        String thumb,
        @JsonProperty("cover_photo") String coverPhoto,
        Boolean verified,
        Boolean highlighted) {
}
