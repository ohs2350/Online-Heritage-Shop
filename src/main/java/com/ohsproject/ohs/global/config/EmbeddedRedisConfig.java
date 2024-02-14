package com.ohsproject.ohs.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Profile("test")
@Configuration
@Slf4j
public class EmbeddedRedisConfig {

    private final RedisServer redisServer;

    public EmbeddedRedisConfig(@Value("${spring.redis.port}") int port) throws IOException {
        this.redisServer = new RedisServer(port);
    }

    @PostConstruct
    public void startRedis() {
        try {
            redisServer.start();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @PreDestroy
    public void stopRedis() {
        this.redisServer.stop();
    }

}
