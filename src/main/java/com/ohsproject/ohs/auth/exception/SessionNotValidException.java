package com.ohsproject.ohs.auth.exception;

public class SessionNotValidException extends RuntimeException {

    public SessionNotValidException() {
        super("세션이 유효하지 않습니다.");
    }
}