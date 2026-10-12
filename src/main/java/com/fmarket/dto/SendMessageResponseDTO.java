package com.fmarket.dto;

import java.time.LocalDateTime;

public record SendMessageResponseDTO(Long id, Long from, Long to, String text, LocalDateTime createdAt) {
}
