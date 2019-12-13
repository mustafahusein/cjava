package com.mustafahusein.errors.exceptions;

public class InterpretationError extends RuntimeException {
    private static final String INTERPRETATION_ERROR = "INTERPRETATION ERROR: %s";

    InterpretationError() {
        super();
    }

    public InterpretationError(String message) {
        super(String.format(INTERPRETATION_ERROR, message));
    }
}
