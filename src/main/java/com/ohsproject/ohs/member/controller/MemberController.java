package com.ohsproject.ohs.member.controller;

import com.ohsproject.ohs.member.dto.request.MemberLoginRequest;
import com.ohsproject.ohs.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> login(@RequestBody @Valid final MemberLoginRequest memberLoginRequest, HttpSession httpSession) {
        memberService.login(memberLoginRequest, httpSession);

        return ResponseEntity.ok().build();
    }

}