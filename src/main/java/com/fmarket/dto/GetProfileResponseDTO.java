package com.fmarket.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetProfileResponseDTO(
        String name,
        String profile,
        String description,
        String tiktok,
        String instagram,
        Boolean verified,
        String thumb,
        @JsonProperty("cover_photo") String coverPhoto,
        List<PostDTO> posts,
        @JsonProperty("plan_value") BigDecimal planValue,
        CountersDTO counters,
        boolean signed) {

    public record PostDTO(
            Long id,
            String content,
            String type,
            String description,
            @JsonProperty("is_private") Boolean isPrivate) {
    }

    public record CountersDTO(
            @JsonProperty("private_midias") long privateMidias,
            long images,
            long videos) {
    }
}
