package com.validation.post.exception;

/**
 * Thrown when input validation fails.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
