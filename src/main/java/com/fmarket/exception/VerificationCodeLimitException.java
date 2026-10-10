package com.fmarket.exception;

public class VerificationCodeLimitException extends RuntimeException {

    public VerificationCodeLimitException(String message) {
        super(message);
    }
}
