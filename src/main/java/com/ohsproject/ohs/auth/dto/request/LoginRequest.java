package com.ohsproject.ohs.auth.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class LoginRequest {
    @NotNull(message = "아이디를 입력해주세요.")
    private Long id;

    private LoginRequest() {
    }

    public LoginRequest(final Long id) {
        this.id = id;
    }
}