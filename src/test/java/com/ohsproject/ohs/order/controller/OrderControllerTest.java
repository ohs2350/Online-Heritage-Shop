package com.ohsproject.ohs.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohsproject.ohs.order.dto.request.OrderCompleteRequest;
import com.ohsproject.ohs.order.dto.request.OrderCreateRequest;
import com.ohsproject.ohs.order.dto.request.OrderDetailRequest;
import com.ohsproject.ohs.order.exception.OrderNotFoundException;
import com.ohsproject.ohs.order.exception.OrderNotValidException;
import com.ohsproject.ohs.order.service.OrderService;
import com.ohsproject.ohs.product.exception.InsufficientStockException;
import com.ohsproject.ohs.product.exception.ProductNotFoundException;
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
        verify(orderService).placeOrder(any(OrderCreateRequest.class), eq(1L));
    }

    @Test
    @DisplayName("잘못된 입력으로 주문 시 요청에 실패한다.")
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
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());;
        verify(orderService, never()).placeOrder(any(OrderCreateRequest.class), eq(1L));
    }

    @Test
    @DisplayName("비로그인으로 주문 요청한 경우 요청에 실패한다.")
    void placeOrderWithoutLogin() throws Exception {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest();
        MockHttpSession session = new MockHttpSession();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCreateRequest))
        );

        // then
        resultActions.andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("재고가 없는 상품에 대해 주문한 경우 요청에 실패한다.")
    void insufficientStockBeforeOrder() throws Exception {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest();
        when(orderService.placeOrder(any(OrderCreateRequest.class), anyLong())).thenThrow(new InsufficientStockException());

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCreateRequest))
        );

        // then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 상품에 대해 주문한 경우 요청에 실패한다.")
    void placeOrderWithNotFoundProduct() throws Exception {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest();
        when(orderService.placeOrder(any(OrderCreateRequest.class), anyLong())).thenThrow(new ProductNotFoundException());

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCreateRequest))
        );

        // then
        resultActions.andExpect(status().isNotFound())
                .andDo(print());
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
        verify(orderService).completeOrder(any(OrderCompleteRequest.class), anyLong(), anyLong());
    }

    @Test
    @DisplayName("잘못 입력된 데이터로 주문 완료 API 요청 시 요청에 실패한다.")
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
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());
        verify(orderService, never()).completeOrder(any(OrderCompleteRequest.class), anyLong(), anyLong());
    }

    @Test
    @DisplayName("세션이 만료된 채로 주문 완료 요청 시 요청에 실패한다.")
    void completeOrderWithoutLogin() throws Exception {
        // given
        OrderCompleteRequest orderCompleteRequest = createSampleOrderCompleteRequest();
        MockHttpSession session = new MockHttpSession();

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/order/{orderId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCompleteRequest))
        );

        // then
        resultActions.andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 주문으로 요청한 경우 요청에 실패한다.")
    void completeOrderWithNotFoundOrder() throws Exception {
        // given
        OrderCompleteRequest orderCompleteRequest = createSampleOrderCompleteRequest();
        doThrow(new OrderNotFoundException()).when(orderService).completeOrder(any(OrderCompleteRequest.class), anyLong(), anyLong());

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/order/{orderId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCompleteRequest))
        );

        // then
        resultActions.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("유효하지 않는 주문으로 요청한 경우 요청에 실패한다.")
    void completeOrderWithNotValidOrder() throws Exception {
        // given
        OrderCompleteRequest orderCompleteRequest = createSampleOrderCompleteRequest();
        doThrow(new OrderNotValidException()).when(orderService).completeOrder(any(OrderCompleteRequest.class), anyLong(), anyLong());

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/order/{orderId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCompleteRequest))
        );

        // then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("주문 후 재고가 부족한 경우 요청에 실패한다.")
    void insufficientStockAfterOrder() throws Exception {
        // given
        OrderCompleteRequest orderCompleteRequest = createSampleOrderCompleteRequest();
        doThrow(new InsufficientStockException()).when(orderService).completeOrder(any(OrderCompleteRequest.class), anyLong(), anyLong());

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/order/{orderId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCompleteRequest))
        );

        // then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());
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
