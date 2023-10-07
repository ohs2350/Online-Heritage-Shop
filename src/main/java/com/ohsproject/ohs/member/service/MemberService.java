package com.ohsproject.ohs.member.service;

import com.ohsproject.ohs.global.exception.DuplicateLoginException;
import com.ohsproject.ohs.member.domain.Member;
import com.ohsproject.ohs.member.domain.MemberRepository;
import com.ohsproject.ohs.member.dto.request.MemberLoginRequest;
import com.ohsproject.ohs.member.exception.MemberNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void login(MemberLoginRequest memberLoginRequest, HttpSession httpSession) {
        Member member = findMemberById(memberLoginRequest.getId());

        if (httpSession.getAttribute("memberId")!= null) {
            throw new DuplicateLoginException();
        }

        httpSession.setAttribute("memberId", member.getId());
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
    }
}