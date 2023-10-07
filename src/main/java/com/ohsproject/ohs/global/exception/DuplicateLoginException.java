package com.ohsproject.ohs.global.exception;

public class DuplicateLoginException extends RuntimeException{
    public DuplicateLoginException() {
        super("중복된 로그인.");
    }
}
