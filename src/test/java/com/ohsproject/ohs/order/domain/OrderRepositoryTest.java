package com.ohsproject.ohs.order.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static com.ohsproject.ohs.Constants.ORDER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("유효기간이 지난 주문은 취소 처리 한다.")
    public void updateUnpaidOrderStatus() {
        // given
        Order order = createSampleOrder();
        Long id = orderRepository.save(order).getId();

        // when
        orderRepository.updateUnpaidOrderStatus(OrderStatus.ORDER, OrderStatus.CANCEL, LocalDateTime.now().plusMinutes(5));

        // then
        Order updateOrder = orderRepository.findById(id).orElse(null);
        assertNotNull(updateOrder);
        assertEquals(updateOrder.getStatus(), OrderStatus.CANCEL);
    }

    @Test
    @DisplayName("주문 가능 기간이 남은 주문에 대해서는 처리하지 않는다.")
    public void notUpdateForPaidOrder() {
        // given
        Order order = createSampleOrder();
        Long id = orderRepository.save(order).getId();

        // when
        orderRepository.updateUnpaidOrderStatus(OrderStatus.ORDER, OrderStatus.CANCEL, LocalDateTime.now().minusMinutes(5));

        // then
        Order updateOrder = orderRepository.findById(id).orElse(null);
        assertNotNull(updateOrder);
        assertEquals(updateOrder.getStatus(), OrderStatus.ORDER);
    }

    private Order createSampleOrder() {
        return Order.builder()
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.ORDER)
                .build();
    }
}
