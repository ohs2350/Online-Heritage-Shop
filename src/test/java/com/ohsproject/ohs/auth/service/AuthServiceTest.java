package com.ohsproject.ohs.auth.service;

import com.ohsproject.ohs.auth.exception.DuplicateLoginException;
import com.ohsproject.ohs.member.domain.Member;
import com.ohsproject.ohs.member.domain.MemberRepository;
import com.ohsproject.ohs.auth.dto.request.LoginRequest;
import com.ohsproject.ohs.member.exception.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

import java.util.Optional;

import static com.ohsproject.ohs.support.fixture.MemberFixture.createMember;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;

    public static final String SESSION_ATTRIBUTE_NAME = "memberId";

    @Test
    @DisplayName("등록된 사용자가 정상적인 요청 시 로그인에 성공한다.")
    void login() {
        // given
        Member member = createMember();
        LoginRequest loginRequest = new LoginRequest(1L);
        MockHttpSession session = new MockHttpSession();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        // when
        authService.login(loginRequest, session);

        // then
        verify(memberRepository, times(1)).findById(anyLong());
        assertEquals(session.getAttribute(SESSION_ATTRIBUTE_NAME), member.getId());
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 로그인하는 경우 예외가 발생한다.")
    void loginWithNotValidMemberId() {
        // given
        LoginRequest loginRequest = new LoginRequest(1L);
        MockHttpSession session = new MockHttpSession();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when, then
        assertThrows(MemberNotFoundException.class, () -> authService.login(loginRequest, session));
    }

    @Test
    @DisplayName("중복 로그인의 경우 예외가 발생한다.")
    void duplicateLogin() {
        // given
        Member member = createMember();
        LoginRequest loginRequest = new LoginRequest(1L);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SESSION_ATTRIBUTE_NAME, member.getId());
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        // when, then
        assertThrows(DuplicateLoginException.class, () -> authService.login(loginRequest, session));
    }
}