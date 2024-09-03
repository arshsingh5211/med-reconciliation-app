package com.arsh.exception;

public class MedicationValidationException extends RuntimeException {
    public MedicationValidationException(String message) {
        super(message);
    }
}
