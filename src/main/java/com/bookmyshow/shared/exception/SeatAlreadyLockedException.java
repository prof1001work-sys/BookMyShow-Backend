package com.bookmyshow.shared.exception;

public class SeatAlreadyLockedException extends BaseException {

    public SeatAlreadyLockedException(String message) {
        super(ErrorCode.SEAT_ALREADY_LOCKED, message);
    }

    public SeatAlreadyLockedException() {
        super(ErrorCode.SEAT_ALREADY_LOCKED, ErrorCode.SEAT_ALREADY_LOCKED.getDefaultMessage());
    }
}
