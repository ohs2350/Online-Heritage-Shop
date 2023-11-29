package com.ohsproject.ohs.global.exception.custom;

public class BadRequestException extends CustomException {

    private static final String STATUS_CODE = "400";

    public BadRequestException(String message) {
        super(STATUS_CODE, message);
    }
}
