package com.ohsproject.ohs.auth.exception;

import com.ohsproject.ohs.global.exception.custom.BadRequestException;

public class DuplicateLoginException extends BadRequestException {
    public DuplicateLoginException() {
        super("로그인은 한 번만 할 수 있습니다.");
    }
}
