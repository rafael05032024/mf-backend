package com.fmarket.dto;

import java.io.InputStream;

public record GetMidiaResponseDTO(
        InputStream stream,
        String contentType) {
}
