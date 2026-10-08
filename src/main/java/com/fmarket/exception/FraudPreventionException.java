package com.fmarket.exception;

public class FraudPreventionException extends RuntimeException {

    public FraudPreventionException(String message) {
        super(message);
    }

    public FraudPreventionException(String message, Throwable cause) {
        super(message, cause);
    }
}
