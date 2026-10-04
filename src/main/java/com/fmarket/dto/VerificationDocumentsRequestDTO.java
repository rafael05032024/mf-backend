package com.fmarket.dto;

public record VerificationDocumentsRequestDTO(
        ImageFileDTO rgFront,
        ImageFileDTO rgBack,
        ImageFileDTO selfieWithRg) {
}
