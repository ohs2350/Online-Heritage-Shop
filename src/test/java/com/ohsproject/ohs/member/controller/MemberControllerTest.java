package com.ohsproject.ohs.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohsproject.ohs.member.dto.request.MemberLoginRequest;
import com.ohsproject.ohs.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    final ObjectMapper objectMapper;

    public MemberControllerTest() {
        this.objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("등록된 사용자가 요청 시 로그인에 성공한다.")
    void login() throws Exception {
        // given
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest(1L);
        MockHttpSession session = new MockHttpSession();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(memberLoginRequest))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("id를 null 값으로 로그인 요청한 경우 예외가 발생한다.")
    void loginWithNotValidId() throws Exception {
        // given
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest(null);
        MockHttpSession session = new MockHttpSession();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(memberLoginRequest))
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }
}