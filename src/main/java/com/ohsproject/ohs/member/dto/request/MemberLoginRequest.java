package com.ohsproject.ohs.member.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class MemberLoginRequest {
    @NotNull
    private Long id;

    private MemberLoginRequest() {
    }

    public MemberLoginRequest(final Long id) {
        this.id = id;
    }
}