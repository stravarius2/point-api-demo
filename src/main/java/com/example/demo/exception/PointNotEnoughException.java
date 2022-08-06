package com.example.demo.exception;

public class PointNotEnoughException extends RuntimeException {

    public PointNotEnoughException() {
        super();
    }

    public PointNotEnoughException(String message) {
        super(message);
    }
}
