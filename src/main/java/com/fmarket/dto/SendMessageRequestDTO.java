package com.fmarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// "to" nulo significa mensagem para o suporte. "from" só é considerado quando o dono do sistema responde sem token.
public record SendMessageRequestDTO(
        Long from,
        Long to,
        @NotBlank @Size(max = 4000) String text) {
}
