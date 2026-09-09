package com.bookmyshow.shared.response;

import com.bookmyshow.shared.filter.CorrelationIdFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.MDC;

import java.time.Instant;

/**
 * Standard API envelope for successful responses.
 *
 * @param <T> Payload data type
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Instant timestamp,
        String traceId
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation successful", data, Instant.now(), currentTraceId());
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, Instant.now(), currentTraceId());
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return new ApiResponse<>(true, message, data, Instant.now(), currentTraceId());
    }

    private static String currentTraceId() {
        String traceId = MDC.get(CorrelationIdFilter.MDC_TRACE_ID_KEY);
        return traceId != null ? traceId : MDC.get(CorrelationIdFilter.MDC_CORRELATION_ID_KEY);
    }
}
