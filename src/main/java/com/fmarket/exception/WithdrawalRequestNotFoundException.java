package com.fmarket.exception;

public class WithdrawalRequestNotFoundException extends RuntimeException {

    public WithdrawalRequestNotFoundException(String message) {
        super(message);
    }
}
