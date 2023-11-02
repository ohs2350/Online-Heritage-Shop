package com.ohsproject.ohs.member.dto.request;

import lombok.Getter;

@Getter
public class MemberInfo {
    private final Long id;

    public MemberInfo(Long id) {
        this.id = id;
    }
}
