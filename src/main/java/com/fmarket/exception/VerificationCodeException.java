package com.fmarket.exception;

import jakarta.ws.rs.core.Response;

public class VerificationCodeException extends RuntimeException {

    private final Response.Status status;

    public VerificationCodeException(String message, Response.Status status) {
        super(message);
        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }
}
