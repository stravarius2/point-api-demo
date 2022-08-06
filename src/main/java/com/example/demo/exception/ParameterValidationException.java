package com.example.demo.exception;

public class ParameterValidationException extends RuntimeException {

    public ParameterValidationException() {
        super();
    }

    public ParameterValidationException(String message) {
        super(message);
    }
}
