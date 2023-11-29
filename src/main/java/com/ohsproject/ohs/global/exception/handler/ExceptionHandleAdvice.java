package com.ohsproject.ohs.global.exception.handler;

import com.ohsproject.ohs.global.exception.custom.CustomException;
import com.ohsproject.ohs.global.exception.reponse.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandleAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(final CustomException e) {
        log.error(e.toString());
        return ResponseEntity
                .status(HttpStatus.valueOf(Integer.parseInt(e.getCode())))
                .body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(final MethodArgumentNotValidException e) {
        log.error(e.toString());

        ExceptionResponse exceptionResponse = ExceptionResponse.from("400", "잘못된 요청입니다.");
        for (FieldError fieldError : e.getFieldErrors()) {
            exceptionResponse.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleUnhandledException(final Exception e) {
        log.error(e.toString());
        ExceptionResponse exceptionResponse = ExceptionResponse.from("500", e.getMessage());
        return ResponseEntity.internalServerError().body(exceptionResponse);
    }
}
