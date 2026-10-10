package com.fmarket.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ListNotificationsResponseDTO(List<NotificationDTO> notifications) {

    public record NotificationDTO(
            Long id,
            String text,
            @JsonProperty("created_at") LocalDateTime createdAt,
            @JsonProperty("readed_at") LocalDateTime readedAt) {
    }
}
