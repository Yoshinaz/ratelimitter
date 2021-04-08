package com.agoda.rate.utils.limitter;

public interface RateLimiter {
    boolean isOverLimit(String key);
}
