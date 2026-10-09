package com.fmarket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponseDTO(String token, Boolean verified, String message) {

    public static LoginResponseDTO ofToken(String token, Boolean verified) {
        return new LoginResponseDTO(token, verified, null);
    }

    public static LoginResponseDTO ofMessage(String message) {
        return new LoginResponseDTO(null, null, message);
    }
}
