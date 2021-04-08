package com.agoda.rate.utils.limitter;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;


class SlidingWindowRateLimitTest {

    @Test
    void shouldLimit(){
        Map<String, LimitRule> rules = new HashMap<>();
        rules.put("room",LimitRule.of(Duration.ofSeconds(10),100,Duration.ofSeconds(5)));
        RateLimiter rateLimiter = new SlidingWindowRateLimit(rules);
        for(int i =0;i<100;i++){
            assertThat(rateLimiter.isOverLimit("room")).isFalse();
        }
        assertThat(rateLimiter.isOverLimit("room")).isTrue();
    }

    @Test
    void shouldDisableWhenOverLimit() throws InterruptedException {
        Map<String, LimitRule> rules = new HashMap<>();
        rules.put("room",LimitRule.of(Duration.ofSeconds(5),100,Duration.ofSeconds(5)));
        RateLimiter rateLimiter = new SlidingWindowRateLimit(rules);
        for(int i =0;i<100;i++){
            assertThat(rateLimiter.isOverLimit("room")).isFalse();
        }
        assertThat(rateLimiter.isOverLimit("room")).isTrue();

        Thread.sleep(Duration.ofSeconds(2).toMillis());
        assertThat(rateLimiter.isOverLimit("room")).isTrue();

    }

    @Test
    void shouldEnableAfterOverLimit() throws InterruptedException {
        Map<String, LimitRule> rules = new HashMap<>();
        rules.put("room",LimitRule.of(Duration.ofSeconds(5),100,Duration.ofSeconds(5)));
        RateLimiter rateLimiter = new SlidingWindowRateLimit(rules);
        for(int i =0;i<100;i++){
            assertThat(rateLimiter.isOverLimit("room")).isFalse();
        }
        assertThat(rateLimiter.isOverLimit("room")).isTrue();

        Thread.sleep(Duration.ofSeconds(5).toMillis()+100);
        assertThat(rateLimiter.isOverLimit("room")).isFalse();
    }
}