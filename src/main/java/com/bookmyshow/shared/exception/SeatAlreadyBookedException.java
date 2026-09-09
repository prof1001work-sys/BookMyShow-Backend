package com.bookmyshow.shared.exception;

public class SeatAlreadyBookedException extends BaseException {

    public SeatAlreadyBookedException(String message) {
        super(ErrorCode.SEAT_ALREADY_BOOKED, message);
    }

    public SeatAlreadyBookedException() {
        super(ErrorCode.SEAT_ALREADY_BOOKED, ErrorCode.SEAT_ALREADY_BOOKED.getDefaultMessage());
    }
}
