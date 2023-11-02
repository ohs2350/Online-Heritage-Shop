package com.ohsproject.ohs.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohsproject.ohs.global.exception.SessionNotValidException;
import com.ohsproject.ohs.order.controller.OrderController;
import com.ohsproject.ohs.order.dto.request.OrderCreateRequest;
import com.ohsproject.ohs.order.dto.request.OrderDetailRequest;
import com.ohsproject.ohs.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static com.ohsproject.ohs.Constants.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    final ObjectMapper objectMapper;

    public OrderControllerTest() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("주문 api 요청 성공")
    void successPlaceOrder() throws Exception {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SESSION_ATTRIBUTE_NAME, MEMBER_ID);

        when(orderService.placeOrder(any(OrderCreateRequest.class), eq(MEMBER_ID))).thenReturn(ORDER_ID);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCreateRequest))
        );

        // then
        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(header().string("Location", is("/api/v1/order/" + ORDER_ID)));
    }

    @Test
    @DisplayName("잘못된 입력으로 주문 시 실패")
    void unSuccessPlaceOrder() throws Exception {
        // given
        OrderCreateRequest orderCreateRequest = createNotValidOrderCreateRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SESSION_ATTRIBUTE_NAME, MEMBER_ID);

        when(orderService.placeOrder(any(OrderCreateRequest.class), eq(MEMBER_ID))).thenReturn(ORDER_ID);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCreateRequest))
        );

        // then
        resultActions.andExpect(status().isBadRequest());
        verify(orderService, times(0)).placeOrder(refEq(orderCreateRequest), eq(MEMBER_ID));
    }

    @Test
    @DisplayName("로그인하지 않은 채 주문")
    void placeOrder_without_login()  {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest();
        MockHttpSession session = new MockHttpSession();

        // when, then
        assertThatThrownBy(() -> mockMvc.perform(
                post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCreateRequest))
        )).hasCause(new SessionNotValidException());
    }

    private OrderCreateRequest createSampleOrderCreateRequest() {
        OrderDetailRequest orderDetailRequest = new OrderDetailRequest(1L, 2);
        List<OrderDetailRequest> orderDetailRequests = new ArrayList<>();
        orderDetailRequests.add(orderDetailRequest);
        return new OrderCreateRequest(orderDetailRequests, 1000);
    }

    private OrderCreateRequest createNotValidOrderCreateRequest() {
        return new OrderCreateRequest(null, 1000);
    }
}
