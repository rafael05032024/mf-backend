package com.fmarket.dto;

/** Mensagem enviada ao frontend pelo WebSocket de eventos. */
public record EventResponseDTO(String type, Object data) {
}
