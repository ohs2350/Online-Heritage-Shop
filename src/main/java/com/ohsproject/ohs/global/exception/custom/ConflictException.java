package com.ohsproject.ohs.global.exception.custom;

public class ConflictException extends CustomException {

    private static final String STATUS_CODE = "409";

    public ConflictException(String message) {
        super(STATUS_CODE, message);
    }
}
