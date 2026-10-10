package com.fmarket.exception;

public class ActionForbiddenException extends RuntimeException {

    public ActionForbiddenException(String message) {
        super(message);
    }
}
