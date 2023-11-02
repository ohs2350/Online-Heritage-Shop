package com.ohsproject.ohs.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohsproject.ohs.member.dto.request.MemberLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.ohsproject.ohs.Constants.MEMBER_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    final ObjectMapper objectMapper;

    public MemberControllerTest() {
        this.objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("로그인 api 호출 성공")
    void login() throws Exception {
        // given
        MemberLoginRequest memberLoginRequest = createSampleMemberLoginDto();
        MockHttpSession session = new MockHttpSession();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(memberLoginRequest))
        );

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("잘못된 RequestDto(id가 null)로 로그인 요청 시 실패")
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

    private MemberLoginRequest createSampleMemberLoginDto() {
        return new MemberLoginRequest(MEMBER_ID);
    }
}