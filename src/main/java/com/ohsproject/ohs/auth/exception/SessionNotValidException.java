package com.ohsproject.ohs.auth.exception;

import com.ohsproject.ohs.global.exception.custom.UnauthorizedException;

public class SessionNotValidException extends UnauthorizedException {

    public SessionNotValidException() {
        super("세션이 유효하지 않습니다.");
    }
}
