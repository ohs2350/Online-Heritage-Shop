package com.ohsproject.ohs.order.domain;

import com.ohsproject.ohs.global.config.QueryDslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static com.ohsproject.ohs.support.fixture.OrderFixture.createOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Import(QueryDslConfig.class)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("유효기간이 지난 미결제 주문은 취소 처리 한다.")
    public void updateStatusForUnpaidOrder() {
        // given
        Order order = createOrder();
        Long id = orderRepository.save(order).getId();

        // when
        orderRepository.updateUnpaidOrderStatus(OrderStatus.ORDER, OrderStatus.CANCEL, LocalDateTime.now().plusMinutes(5));

        // then
        Order updateOrder = orderRepository.findById(id).orElse(null);
        assertNotNull(updateOrder);
        assertEquals(updateOrder.getStatus(), OrderStatus.CANCEL);
    }

    @Test
    @DisplayName("유효기간이 남은 미결제 주문에 대해서는 처리하지 않는다.")
    public void notUpdateForExtraPeriodOrder() {
        // given
        Order order = createOrder();
        Long id = orderRepository.save(order).getId();

        // when
        orderRepository.updateUnpaidOrderStatus(OrderStatus.ORDER, OrderStatus.CANCEL, LocalDateTime.now().minusMinutes(5));

        // then
        Order updateOrder = orderRepository.findById(id).orElse(null);
        assertNotNull(updateOrder);
        assertEquals(updateOrder.getStatus(), order.getStatus());
    }

    @Test
    @DisplayName("완료된 주문에 대해서는 처리하지 않는다.")
    public void notUpdateForPaidOrder() {
        // given
        Order order = createOrder(OrderStatus.ORDER_COMPLETE);
        Long id = orderRepository.save(order).getId();

        // when
        orderRepository.updateUnpaidOrderStatus(OrderStatus.ORDER, OrderStatus.CANCEL, LocalDateTime.now().plusMinutes(5));

        // then
        Order updateOrder = orderRepository.findById(id).orElse(null);
        assertNotNull(updateOrder);
        assertEquals(updateOrder.getStatus(), order.getStatus());
    }

    @Test
    @DisplayName("이미 취소된 주문에 대해서는 처리하지 않는다.")
    public void notUpdateForCanceledOrder() {
        // given
        Order order = createOrder(OrderStatus.CANCEL);
        Long id = orderRepository.save(order).getId();

        // when
        orderRepository.updateUnpaidOrderStatus(OrderStatus.ORDER, OrderStatus.CANCEL, LocalDateTime.now().plusMinutes(5));

        // then
        Order updateOrder = orderRepository.findById(id).orElse(null);
        assertNotNull(updateOrder);
        assertEquals(updateOrder.getStatus(), order.getStatus());
    }
}
