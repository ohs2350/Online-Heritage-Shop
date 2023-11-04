package com.ohsproject.ohs.order.job;

import com.ohsproject.ohs.global.constant.OrderValidTime;
import com.ohsproject.ohs.global.redis.PaymentProductOperation;
import com.ohsproject.ohs.order.domain.OrderRepository;
import com.ohsproject.ohs.order.domain.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Profile("main")
@Component
public class HandleUnpaidOrderJob {

    private static final Logger log = LoggerFactory.getLogger(HandleUnpaidOrderJob.class);

    private final PaymentProductOperation paymentProductOperation;

    private final OrderRepository orderRepository;

    public HandleUnpaidOrderJob(PaymentProductOperation paymentProductOperation, OrderRepository orderRepository) {
        this.paymentProductOperation = paymentProductOperation;
        this.orderRepository = orderRepository;
    }

    @Scheduled(fixedDelay = 1800000)
    public void execute() {
        LocalDateTime validOrderDate = LocalDateTime.now().minusMinutes(OrderValidTime.COMMON.getMinutes());
        paymentProductOperation.expireUnpaidProduct();
        orderRepository.updateUnpaidOrderStatus(OrderStatus.ORDER, OrderStatus.CANCEL, validOrderDate);
        log.info("HandleUnpaidOrderJob : 미결제 주문 처리 완료");
    }
}
