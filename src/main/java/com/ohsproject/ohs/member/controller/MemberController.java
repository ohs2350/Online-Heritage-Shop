package com.ohsproject.ohs.member.controller;

import com.ohsproject.ohs.member.dto.request.MemberLoginDto;
import com.ohsproject.ohs.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid final MemberLoginDto memberLoginDto, HttpSession httpSession) {
        memberService.login(memberLoginDto, httpSession);

        return ResponseEntity.ok().build();
    }
}