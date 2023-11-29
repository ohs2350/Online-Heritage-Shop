package com.ohsproject.ohs.member.exception;

import com.ohsproject.ohs.global.exception.custom.NotFoundException;

public class MemberNotFoundException extends NotFoundException {
    public MemberNotFoundException() {
        super("회원을 찾을 수 없습니다.");
    }

}
