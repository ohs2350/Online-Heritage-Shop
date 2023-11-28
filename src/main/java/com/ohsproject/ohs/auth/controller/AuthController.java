package com.ohsproject.ohs.auth.controller;

import com.ohsproject.ohs.auth.dto.request.LoginRequest;
import com.ohsproject.ohs.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid final LoginRequest loginRequest, HttpSession httpSession) {
        authService.login(loginRequest, httpSession);

        return ResponseEntity.ok().build();
    }

}