package com.ohsproject.ohs.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohsproject.ohs.auth.dto.request.LoginRequest;
import com.ohsproject.ohs.auth.exception.DuplicateLoginException;
import com.ohsproject.ohs.auth.service.AuthService;
import com.ohsproject.ohs.member.exception.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.HttpSession;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    final ObjectMapper objectMapper;

    public AuthControllerTest() {
        this.objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("등록된 사용자가 요청 시 로그인에 성공한다.")
    void login() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest(1L);
        MockHttpSession session = new MockHttpSession();
        doNothing().when(authService).login(any(LoginRequest.class), any(HttpSession.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(loginRequest))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(print());
        verify(authService).login(any(LoginRequest.class), any(HttpSession.class));
    }

    @Test
    @DisplayName("null 값인 id로 로그인을 요청한 경우 로그인에 실패한다.")
    void loginWithNotValidId() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest(null);
        MockHttpSession session = new MockHttpSession();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(loginRequest))
        );

        // then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());;
    }

    @Test
    @DisplayName("존재하지 않는 id로 로그인을 요청한 경우 로그인에 실패한다.")
    void loginWithNotFoundId() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest(1L);
        MockHttpSession session = new MockHttpSession();
        doThrow(new MemberNotFoundException()).when(authService).login(any(LoginRequest.class), any(HttpSession.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(loginRequest))
        );

        // then
        resultActions.andExpect(status().isNotFound())
                .andDo(print());;
    }

    @Test
    @DisplayName("중복 로그인 시 실패한다.")
    void duplicateLogin() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest(1L);
        MockHttpSession session = new MockHttpSession();
        doThrow(new DuplicateLoginException()).when(authService).login(any(LoginRequest.class), any(HttpSession.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(loginRequest))
        );

        // then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());;
    }
}