package com.fmarket.dto;

import java.time.LocalDateTime;

public record CreateSignatureResponseDTO(Long id, Long producer, Long subscriber, LocalDateTime expireAt) {
}
