package com.increff.pos.exception;

public class ApiException extends Exception {
    private static final long serialVersionUID = 1L;

    public ApiException(String message) {
        super(message);
    }
} 