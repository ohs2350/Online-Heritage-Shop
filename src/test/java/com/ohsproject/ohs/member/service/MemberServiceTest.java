package com.ohsproject.ohs.member.service;

import com.ohsproject.ohs.global.exception.DuplicateLoginException;
import com.ohsproject.ohs.member.domain.Member;
import com.ohsproject.ohs.member.domain.MemberRepository;
import com.ohsproject.ohs.member.dto.request.MemberLoginRequest;
import com.ohsproject.ohs.member.exception.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

import java.util.Optional;

import static com.ohsproject.ohs.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("로그인 성공 후 세션에 id 저장")
    void login() {
        // given
        Member member = createSampleMember();
        MemberLoginRequest memberLoginRequest = createSampleMemberLoginDto();
        MockHttpSession session = new MockHttpSession();
        when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));

        // when
        memberService.login(memberLoginRequest, session);

        // then
        verify(memberRepository, times(1)).findById(anyLong());
        assertEquals(session.getAttribute(SESSION_ATTRIBUTE_NAME), member.getId());
    }

    @Test
    @DisplayName("잘못된 아이디로 로그인하는 경우 예외를 발생 시킨다.")
    void loginWithNotValidMember() {
        // given
        MemberLoginRequest memberLoginRequest = createSampleMemberLoginDto();
        MockHttpSession session = new MockHttpSession();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when, then
        assertThrows(MemberNotFoundException.class, () -> memberService.login(memberLoginRequest, session));
    }

    @Test
    @DisplayName("중복 로그인한 경우 예외를 발생 시킨다.")
    void duplicateLogin() {
        // given
        Member member = createSampleMember();
        MemberLoginRequest memberLoginRequest = createSampleMemberLoginDto();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SESSION_ATTRIBUTE_NAME, member.getId());
        when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));

        // when, then
        assertThrows(DuplicateLoginException.class, () -> memberService.login(memberLoginRequest, session));
    }


    private Member createSampleMember() {
        return new Member(MEMBER_ID, "test");
    }

    private MemberLoginRequest createSampleMemberLoginDto() {
        return new MemberLoginRequest(MEMBER_ID);
    }

}