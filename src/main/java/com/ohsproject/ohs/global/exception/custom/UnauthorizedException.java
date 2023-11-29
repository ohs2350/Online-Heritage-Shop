package com.ohsproject.ohs.global.exception.custom;

public class UnauthorizedException extends CustomException{

    private static final String STATUS_CODE = "401";

    public UnauthorizedException(String message) {
        super(STATUS_CODE, message);
    }
}
