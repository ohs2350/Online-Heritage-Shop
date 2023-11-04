package com.ohsproject.ohs.global.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static com.ohsproject.ohs.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentProductOperationTest {

    @Autowired
    private PaymentProductOperation paymentProductOperation;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    @DisplayName("결제한 상품의 수량만큼 redis 에 add 한다.")
    void add() {
        // given
        PaymentProduct product1 =  new PaymentProduct(PRODUCT_ID_1ST, MEMBER_ID, PRODUCT_STOCK_1ST);

        // when
        paymentProductOperation.add(product1);

        // then
        Long count = paymentProductOperation.count(PRODUCT_ID_1ST);
        assertEquals(PRODUCT_STOCK_1ST, count);
    }

    @Test
    @DisplayName("사용자가 주문한 모든 상품을 제거한다.")
    public void remove() {
        // given
        PaymentProduct product =  new PaymentProduct(PRODUCT_ID_1ST, MEMBER_ID, PRODUCT_STOCK_1ST);
        paymentProductOperation.add(product);

        // when
        paymentProductOperation.remove(product);

        // then
        Long count = paymentProductOperation.count(PRODUCT_ID_1ST);
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

}
