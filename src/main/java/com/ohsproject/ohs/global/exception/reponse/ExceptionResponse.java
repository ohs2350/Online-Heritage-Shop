package com.ohsproject.ohs.global.exception.reponse;

import com.ohsproject.ohs.global.exception.custom.CustomException;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ExceptionResponse {
    private String code;
    private String message;
    private Map<String, String> fieldErrors;

    private ExceptionResponse() {
    }

    private ExceptionResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.fieldErrors = new HashMap<>();
    }

    public static ExceptionResponse from(CustomException e) {
        return new ExceptionResponse(e.getCode(), e.getMessage());
    }

    public static ExceptionResponse from(String code, String message) {
        return new ExceptionResponse(code, message);
    }

    public void addFieldError(String field, String message) {
        fieldErrors.put(field, message);
    }
}
