package com.bookmyshow.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

/**
 * Standard structured API error response.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        String traceId,
        Map<String, String> errors
) {
    public ErrorResponse(int status, String code, String message, String path, String traceId) {
        this(Instant.now(), status, code, message, path, traceId, null);
    }

    public ErrorResponse(int status, String code, String message, String path, String traceId, Map<String, String> errors) {
        this(Instant.now(), status, code, message, path, traceId, errors);
    }
}
