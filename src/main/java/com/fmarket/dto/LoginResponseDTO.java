package com.fmarket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponseDTO(String token, String message) {

    public static LoginResponseDTO ofToken(String token) {
        return new LoginResponseDTO(token, null);
    }

    public static LoginResponseDTO ofMessage(String message) {
        return new LoginResponseDTO(null, message);
    }
}
