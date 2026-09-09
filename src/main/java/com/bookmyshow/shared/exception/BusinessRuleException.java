package com.bookmyshow.shared.exception;

public class BusinessRuleException extends BaseException {

    public BusinessRuleException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public BusinessRuleException(String message) {
        super(ErrorCode.VALIDATION_FAILED, message);
    }
}
