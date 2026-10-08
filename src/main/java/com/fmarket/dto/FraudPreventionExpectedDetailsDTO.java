package com.fmarket.dto;

public record FraudPreventionExpectedDetailsDTO(
        String firstName,
        String lastName,
        String dateOfBirth,
        String document) {
}
