package com.demo.elevator.exception;

public class CapacityExceededException extends Exception {

    private static final long serialVersionUID = 1L;

    public CapacityExceededException(String message) {
        super(message);
    }
}
