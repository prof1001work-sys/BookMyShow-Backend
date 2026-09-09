package com.bookmyshow.shared.exception;

public class ConflictException extends BaseException {

    public ConflictException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ConflictException(String message) {
        super(ErrorCode.CONCURRENCY_FAILURE, message);
    }
}
