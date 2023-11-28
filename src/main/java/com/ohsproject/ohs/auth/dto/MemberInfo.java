package com.ohsproject.ohs.auth.dto;

import lombok.Getter;

@Getter
public class MemberInfo {
    private final Long id;

    public MemberInfo(Long id) {
        this.id = id;
    }
}
