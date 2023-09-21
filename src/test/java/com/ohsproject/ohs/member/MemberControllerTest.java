package com.ohsproject.ohs.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohsproject.ohs.member.controller.MemberController;
import com.ohsproject.ohs.member.dto.request.MemberLoginDto;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    final ObjectMapper objectMapper;

    private static final long MEMBER_ID = 1L;

    public MemberControllerTest() {
        this.objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("로그인 api 호출 성공")
    void success_login() throws Exception {
        // given
        MemberLoginDto memberLoginDto = createSampleMemberLoginDto();
        MockHttpSession session = new MockHttpSession();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(memberLoginDto))
        );

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("잘못된 ID로 로그인 시 실패")
    void unSuccess_login() throws Exception {
        // given
        MemberLoginDto memberLoginDto = new MemberLoginDto(null);
        MockHttpSession session = new MockHttpSession();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(memberLoginDto))
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    private MemberLoginDto createSampleMemberLoginDto() {
        return new MemberLoginDto(MEMBER_ID);
    }
}
