package com.test.ordertest.application.exception;

public class OrderException extends RuntimeException{
    public static final int OUT_OF_BUSINESS_TIME = 300;
    public static final int NOT_RECORD_FOUND_FOR_ID = 301;
    public static final int NOT_RECORD_FOUND_FOR_ITEM_ID = 302;

    private int errorCode;

    public OrderException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
