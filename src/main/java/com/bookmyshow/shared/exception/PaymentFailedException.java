package com.bookmyshow.shared.exception;

public class PaymentFailedException extends BaseException {

    public PaymentFailedException(String message) {
        super(ErrorCode.PAYMENT_FAILED, message);
    }

    public PaymentFailedException(String message, Throwable cause) {
        super(ErrorCode.PAYMENT_FAILED, message, cause);
    }
}
