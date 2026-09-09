package com.bookmyshow.shared.exception;

public class InvalidBookingStateException extends BaseException {

    public InvalidBookingStateException(String message) {
        super(ErrorCode.INVALID_BOOKING_STATE, message);
    }
}
