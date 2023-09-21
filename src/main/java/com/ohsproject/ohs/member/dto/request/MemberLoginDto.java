package com.ohsproject.ohs.member.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class MemberLoginDto {
    @NotNull
    private Long id;

    private MemberLoginDto() {
    }

    public MemberLoginDto(final Long id) {
        this.id = id;
    }
}