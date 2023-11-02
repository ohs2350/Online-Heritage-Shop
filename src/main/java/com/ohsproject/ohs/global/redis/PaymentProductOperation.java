package com.ohsproject.ohs.global.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentProductOperation {
    private final RedisTemplate<String, Object> redisTemplate;

    public PaymentProductOperation(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * ZADD Operation
     */
    public void add(PaymentProduct product) {
        String key = getKey(product.getProductId());
        double score = getScore();

        for (int i = 1; i <= product.getQty(); i++) {
            String value = getValue(product.getMemberId(), i);
            redisTemplate.opsForZSet().add(key, value, score);
        }
    }

    /**
     * ZCOUNT Operation
     */
    public Long count(Long productId) {
        String key = getKey(productId);
        return redisTemplate.opsForZSet().count(key, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /**
     * ZREM Operation
     */
    public void remove(PaymentProduct product) {
        String key = getKey(product.getProductId());

        for (int i = 1; i <= product.getQty(); i++) {
            String value = getValue(product.getMemberId(), i);
            redisTemplate.opsForZSet().remove(key, value);
        }
    }

    /**
     * ZREMRANGEBYSCORE Operation
     */
    public void removeRangeByScore(Long productId) {
        String key = getKey(productId);
        double now = (double) (System.currentTimeMillis() / 1000);

        redisTemplate.opsForZSet().removeRangeByScore(key, Double.NEGATIVE_INFINITY, now);
    }

    private String getKey(Long productId) {
        return "product:" + productId;
    }

    private String getValue(Long memberId, int index) {
        return "member:" + memberId + ":seq:" + index;
    }

    private double getScore() {
        int ttl = 1800;
        long unixTimestamp = System.currentTimeMillis() / 1000;

        return unixTimestamp + ttl;
    }
}
