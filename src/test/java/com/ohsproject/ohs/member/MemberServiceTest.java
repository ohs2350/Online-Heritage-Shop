package com.ohsproject.ohs.member;

import com.ohsproject.ohs.member.domain.Member;
import com.ohsproject.ohs.member.domain.MemberRepository;
import com.ohsproject.ohs.member.dto.request.MemberLoginRequest;
import com.ohsproject.ohs.member.exception.MemberNotFoundException;
import com.ohsproject.ohs.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    private static final long MEMBER_ID = 1L;

    @Test
    @DisplayName("로그인 성공")
    void login() {
        // given
        Member member = createSampleMember();
        MemberLoginRequest memberLoginRequest = createSampleMemberLoginDto();
        MockHttpSession session = new MockHttpSession();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when
        memberService.login(memberLoginRequest, session);

        // then
        verify(memberRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("잘못된 아이디로 로그인하는 경우")
    void login_MemberNotFound() {
        // given
        MemberLoginRequest memberLoginRequest = createSampleMemberLoginDto();
        MockHttpSession session = new MockHttpSession();
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        // when, then
        assertThrows(MemberNotFoundException.class, () -> memberService.login(memberLoginRequest, session));
    }


    private Member createSampleMember() {
        return new Member(MEMBER_ID, "test");
    }

    private MemberLoginRequest createSampleMemberLoginDto() {
        return new MemberLoginRequest(MEMBER_ID);
    }

}