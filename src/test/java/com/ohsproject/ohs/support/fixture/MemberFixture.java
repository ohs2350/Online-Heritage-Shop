package com.ohsproject.ohs.support.fixture;

import com.ohsproject.ohs.member.domain.Member;

public class MemberFixture {

    public static final Long MEMBER_ID = 1L;
    public static final String MEMBER_NAME = "test";

    public static Member createMember() {
        return new Member(MEMBER_ID, MEMBER_NAME);
    }
}
