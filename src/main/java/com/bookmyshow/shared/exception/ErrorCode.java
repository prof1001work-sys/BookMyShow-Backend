package com.bookmyshow.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Standard business error codes for BookMyShow Modular Monolith.
 */
public enum ErrorCode {
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND, "The requested resource was not found"),
    SEAT_ALREADY_LOCKED("SEAT_ALREADY_LOCKED", HttpStatus.CONFLICT, "One or more selected seats are currently locked by another user"),
    SEAT_ALREADY_BOOKED("SEAT_ALREADY_BOOKED", HttpStatus.CONFLICT, "One or more selected seats are already booked"),
    INVALID_BOOKING_STATE("INVALID_BOOKING_STATE", HttpStatus.BAD_REQUEST, "Invalid booking state transition"),
    PAYMENT_FAILED("PAYMENT_FAILED", HttpStatus.BAD_REQUEST, "Payment processing failed"),
    INVALID_COUPON("INVALID_COUPON", HttpStatus.BAD_REQUEST, "Coupon is invalid or expired"),
    UNAUTHORIZED("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "Authentication is required to access this resource"),
    FORBIDDEN("FORBIDDEN", HttpStatus.FORBIDDEN, "Access to this resource is denied"),
    VALIDATION_FAILED("VALIDATION_FAILED", HttpStatus.BAD_REQUEST, "Validation failed for request parameters"),
    CONCURRENCY_FAILURE("CONCURRENCY_FAILURE", HttpStatus.CONFLICT, "Conflict detected due to concurrent updates"),
    IDEMPOTENCY_VIOLATION("IDEMPOTENCY_VIOLATION", HttpStatus.CONFLICT, "Concurrent or duplicate idempotent request in progress"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred");

    private final String code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(String code, HttpStatus httpStatus, String defaultMessage) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
