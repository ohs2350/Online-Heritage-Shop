package com.ohsproject.ohs.global.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static com.ohsproject.ohs.support.fixture.MemberFixture.MEMBER_ID;
import static com.ohsproject.ohs.support.fixture.ProductFixture.PRODUCT_ID;
import static com.ohsproject.ohs.support.fixture.ProductFixture.PRODUCT_STOCK;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentProductOperationTest {

    @Autowired
    private PaymentProductOperation paymentProductOperation;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    @DisplayName("상품의 수량만큼 redis 에 저장한다.")
    void add() {
        // given
        PaymentProduct product = createPaymentProduct();

        // when
        paymentProductOperation.add(product);

        // then
        Long count = paymentProductOperation.count(product.getProductId());
        assertEquals(product.getQty(), count);
    }

    @Test
    @DisplayName("사용자가 주문한 상품의 수량만큼 redis 에서 제거한다.")
    public void remove() {
        // given
        PaymentProduct product = createPaymentProduct();
        paymentProductOperation.add(product);

        // when
        paymentProductOperation.remove(product);

        // then
        Long count = paymentProductOperation.count(product.getProductId());
        assertEquals(0, count);
    }

    @Test
    @DisplayName("주문 유효기간이 지난 상품을 모두 제거한다.")
    public void removeRangeByScore() {
        // given
        String testKey = "product:testProduct1";
        long score = (System.currentTimeMillis() / 1000);
        redisTemplate.opsForZSet().add(testKey, "test", score);

        // when
        paymentProductOperation.removeRangeByScore(testKey);

        // then
        Long count = redisTemplate.opsForZSet().count(testKey, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        assertEquals(0, count);
    }

    private PaymentProduct createPaymentProduct() {
        return PaymentProduct.builder()
                .productId(PRODUCT_ID)
                .memberId(MEMBER_ID)
                .qty(PRODUCT_STOCK)
                .build();
    }
}
