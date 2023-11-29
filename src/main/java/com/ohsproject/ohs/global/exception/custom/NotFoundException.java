package com.ohsproject.ohs.global.exception.custom;

public class NotFoundException extends CustomException {

    private static final String STATUS_CODE = "404";

    public NotFoundException(String message) {
        super(STATUS_CODE, message);
    }
}
