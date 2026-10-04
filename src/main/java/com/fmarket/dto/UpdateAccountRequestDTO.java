package com.fmarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record UpdateAccountRequestDTO(
        @JsonProperty("real_name") String realName,
        LocalDate birthdate,
        String document,
        String name,
        String profile,
        String description,
        String tiktok,
        String instagram) {
}
