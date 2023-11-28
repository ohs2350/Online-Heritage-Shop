package com.ohsproject.ohs.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohsproject.ohs.auth.exception.SessionNotValidException;
import com.ohsproject.ohs.order.dto.request.OrderCompleteRequest;
import com.ohsproject.ohs.order.dto.request.OrderCreateRequest;
import com.ohsproject.ohs.order.dto.request.OrderDetailRequest;
import com.ohsproject.ohs.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
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

import static com.ohsproject.ohs.support.fixture.ProductFixture.PRODUCT_ID;
import static com.ohsproject.ohs.support.fixture.ProductFixture.PRODUCT_STOCK;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private final ObjectMapper objectMapper;

    private final MockHttpSession session;

    public OrderControllerTest() {
        objectMapper = new ObjectMapper();
        session = new MockHttpSession();
    }

    @BeforeEach
    void setup() {
        session.setAttribute("memberId", 1L);
    }

    @Test
    @DisplayName("주문 api 요청 성공")
    void placeOrder() throws Exception {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest();
        when(orderService.placeOrder(any(OrderCreateRequest.class), anyLong())).thenReturn(1L);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCreateRequest))
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Location", is("/api/v1/order/" + 1L)))
                .andDo(print());
    }

    @Test
    @DisplayName("잘못된 입력으로 주문 시 예외가 발생한다.")
    void placeOrderWithNotValidRequest() throws Exception {
        // given
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(null, 1000);
        when(orderService.placeOrder(any(OrderCreateRequest.class), anyLong())).thenReturn(1L);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCreateRequest))
        );

        // then
        resultActions.andExpect(status().isBadRequest());
        verify(orderService, times(0)).placeOrder(refEq(orderCreateRequest), eq(1L));
    }

    @Test
    @DisplayName("비로그인으로 주문 요청한 경우 예외가 발생한다.")
    void placeOrderWithoutLogin()  {
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

        // TODO - 예외 헨들링 후 상태코드로 반환
    }

    @Test
    @DisplayName("주문 완료 api 요청 성공")
    public void completeOrder() throws Exception {
        // given
        OrderCompleteRequest orderCompleteRequest = createSampleOrderCompleteRequest();
        doNothing().when(orderService).completeOrder(any(OrderCompleteRequest.class), anyLong(), anyLong());

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/order/{orderId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCompleteRequest))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("잘못된 데이터로 주문 완료 API 요청 시 예외가 발생한다.")
    void completeOrderWithNotValidRequest() throws Exception {
        // given
        OrderCompleteRequest orderCompleteRequest = new OrderCompleteRequest(null, 1000);
        doNothing().when(orderService).completeOrder(any(OrderCompleteRequest.class), anyLong(), anyLong());

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/order/{orderId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCompleteRequest))
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("세션이 만료된 채로 주문 완료 요청 시 예외가 발생한다.")
    void completeOrderWithoutLogin()  {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest();
        MockHttpSession session = new MockHttpSession();

        // when, then
        assertThatThrownBy(() -> mockMvc.perform(
                put("/api/v1/order/{orderId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCreateRequest))
        )).hasCause(new SessionNotValidException());

        // TODO - 예외 헨들링 후 상태코드로 반환
    }

    private OrderCreateRequest createSampleOrderCreateRequest() {
        OrderDetailRequest orderDetailRequest = new OrderDetailRequest(PRODUCT_ID, PRODUCT_STOCK);
        List<OrderDetailRequest> orderDetailRequests = new ArrayList<>(List.of(orderDetailRequest));
        return new OrderCreateRequest(orderDetailRequests, 1000);
    }

    private OrderCompleteRequest createSampleOrderCompleteRequest() {
        OrderDetailRequest orderDetailRequest = new OrderDetailRequest(PRODUCT_ID, PRODUCT_STOCK);
        List<OrderDetailRequest> orderDetailRequests = new ArrayList<>(List.of(orderDetailRequest));
        return new OrderCompleteRequest(orderDetailRequests, 1000);
    }
}
