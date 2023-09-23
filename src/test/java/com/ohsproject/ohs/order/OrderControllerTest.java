package com.ohsproject.ohs.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohsproject.ohs.member.domain.Member;
import com.ohsproject.ohs.order.controller.OrderController;
import com.ohsproject.ohs.order.dto.request.OrderCreateRequest;
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

    private static final long ORDER_ID = 1L;

    public OrderControllerTest() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("주문 api 성공")
    void successCreateOrder() throws Exception {
        // given
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", new Member(1L,"test"));

        when(orderService.placeOrder(any(OrderCreateRequest.class))).thenReturn(ORDER_ID);

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
        verify(orderService, times(1)).placeOrder(refEq(orderCreateRequest));
    }

    @Test
    @DisplayName("잘못된 입력으로 주문 시 실패")
    void unSuccessCreateOrder() throws Exception {
        // given
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(null, null, null, 1000);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", new Member(1L,"test"));

        when(orderService.placeOrder(any(OrderCreateRequest.class))).thenReturn(ORDER_ID);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .content(objectMapper.writeValueAsString(orderCreateRequest))
        );

        // then
        resultActions.andExpect(status().isBadRequest());
        verify(orderService, times(0)).placeOrder(refEq(orderCreateRequest));
    }

    private OrderCreateRequest createSampleOrderCreateRequest() {
        List<Long> productIds = new ArrayList<Long>(1);
        List<Integer> quantities = new ArrayList<Integer>(1);
        return new OrderCreateRequest(1L, productIds, quantities, 1000);
    }
}
