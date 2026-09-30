package com.payment_gateway.razorpay.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    /**
     * Creates the Redis template used by components that store string keys and serialized string values.
     *
     * @param redisConnectionFactory application Redis connection factory
     * @return a template bound to that factory
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory); //to establish redis connection
    }
}
