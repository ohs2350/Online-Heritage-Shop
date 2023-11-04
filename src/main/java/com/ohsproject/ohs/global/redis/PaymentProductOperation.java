package com.ohsproject.ohs.global.redis;

import com.ohsproject.ohs.global.constant.OrderValidTime;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

@Component
public class PaymentProductOperation {

    private static final String KEY_PREFIX = "product:";
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
    public void removeRangeByScore(String key) {
        double now = (double) getUnixTimestamp();

        redisTemplate.opsForZSet().removeRangeByScore(key, Double.NEGATIVE_INFINITY, now);
    }

    public void expireUnpaidProduct() {
        ScanOptions options = ScanOptions.scanOptions()
                .match(KEY_PREFIX+"*")
                .count(100)
                .type(DataType.ZSET)
                .build();
        Cursor<String> cursor = redisTemplate.scan(options);

        while (cursor.hasNext()) {
            String key = cursor.next();
            removeRangeByScore(key);
        }
        cursor.close();
    }

    private String getKey(Long productId) {
        return KEY_PREFIX + productId;
    }

    private String getValue(Long memberId, int index) {
        return "member:" + memberId + ":seq:" + index;
    }

    private double getScore() {
        int ttl = OrderValidTime.COMMON.getSeconds();
        long unixTimestamp = getUnixTimestamp();

        return unixTimestamp + ttl;
    }

    private long getUnixTimestamp() {
        return System.currentTimeMillis() / 1000;
    }
}
