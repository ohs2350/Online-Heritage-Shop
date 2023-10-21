package com.ohsproject.ohs.order;

import com.ohsproject.ohs.member.domain.Member;
import com.ohsproject.ohs.member.domain.MemberRepository;
import com.ohsproject.ohs.order.dto.request.OrderCreateRequest;
import com.ohsproject.ohs.order.service.OrderService;
import com.ohsproject.ohs.product.domain.Product;
import com.ohsproject.ohs.product.domain.ProductRepository;
import com.ohsproject.ohs.product.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderServiceLockTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Member member = new Member(1L, "test");
        memberRepository.save(member);
        Product product = createSampleProduct();
        productRepository.save(product);
    }

    @Test
    @DisplayName("여러명의 사용자 동시 주문 테스트")
    void placeOrder_With_MultiUser() throws InterruptedException {
        OrderCreateRequest orderCreateRequest = createSampleOrderCreateRequest();
        int numberOfThreads = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    orderService.placeOrder(orderCreateRequest);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Product product = productRepository.findById(1L)
                .orElseThrow(ProductNotFoundException::new);

        assertThat(product.getStock()).isZero();
        System.out.println("재고량 = " + product.getStock());
    }

    private OrderCreateRequest createSampleOrderCreateRequest() {
        List<Long> productIds = List.of(1L);
        List<Integer> quantities = List.of(1);
        return new OrderCreateRequest(1L, productIds, quantities, 1000);
    }

    private Product createSampleProduct() {
        return Product.builder()
                .id(1L)
                .price(1000)
                .stock(5)
                .build();
    }

}
