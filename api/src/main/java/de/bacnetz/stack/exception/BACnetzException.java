package de.bacnetz.stack.exception;

public class BACnetzException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // Parameterless Constructor
    public BACnetzException() {
    }

    // Constructor that accepts a message
    public BACnetzException(final String message) {
        super(message);
    }
}
