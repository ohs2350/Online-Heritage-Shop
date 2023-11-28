package com.ohsproject.ohs.auth.service;

import com.ohsproject.ohs.auth.exception.DuplicateLoginException;
import com.ohsproject.ohs.member.domain.Member;
import com.ohsproject.ohs.member.domain.MemberRepository;
import com.ohsproject.ohs.auth.dto.request.LoginRequest;
import com.ohsproject.ohs.member.exception.MemberNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@Service
@Transactional(readOnly = true)
public class AuthService {
    private final MemberRepository memberRepository;

    private static final String SESSION_ATTRIBUTE_NAME = "memberId";

    public AuthService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void login(LoginRequest loginRequest, HttpSession httpSession) {
        Member member = memberRepository.findById(loginRequest.getId())
                .orElseThrow(MemberNotFoundException::new);

        if (httpSession.getAttribute(SESSION_ATTRIBUTE_NAME)!= null) {
            throw new DuplicateLoginException();
        }

        httpSession.setAttribute(SESSION_ATTRIBUTE_NAME, member.getId());
    }
}