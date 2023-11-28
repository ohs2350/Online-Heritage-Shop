package com.ohsproject.ohs.auth.exception;

public class DuplicateLoginException extends RuntimeException{
    public DuplicateLoginException() {
        super("중복된 로그인.");
    }
}
