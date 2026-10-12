package com.fmarket.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ListMessagesResponseDTO(List<MessageDTO> messages) {

    public record MessageDTO(
            Long id,
            Long from,
            Long to,
            String text,
            @JsonProperty("created_at") LocalDateTime createdAt,
            @JsonProperty("readed_at") LocalDateTime readedAt) {
    }
}
