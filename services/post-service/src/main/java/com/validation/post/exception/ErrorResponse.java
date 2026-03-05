package com.validation.post.exception;

import java.time.LocalDateTime;

/**
 * Standard error response body.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}
