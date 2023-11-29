package com.ohsproject.ohs.global.exception.custom;

public class ForbiddenException extends CustomException {

    private static final String STATUS_CODE = "403";

    public ForbiddenException(String message) {
        super(STATUS_CODE, message);
    }
}
