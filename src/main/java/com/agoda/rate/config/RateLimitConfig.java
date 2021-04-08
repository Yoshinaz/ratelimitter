package com.agoda.rate.config;


import com.agoda.rate.utils.limitter.LimitRule;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "ratelimit",ignoreUnknownFields = false)
@PropertySource(value = "classpath:application.yaml", factory = YamlPropertySourceFactory.class)
@Getter
@Setter
public class RateLimitConfig {

    private Map<String,List<RateLimit>> rates;
//    private List<RateLimit> rates;

    @Getter
    @Setter
    public static class RateLimit{
        private String key;
        private Integer duration_sec;
        private Integer limit;
        private Integer pause_sec;
    }

    public Map<String, LimitRule> getRateConfig(String controlKey){
        Map<String, LimitRule> rules = new HashMap<>();
        List<RateLimit> rateList =  rates.get(controlKey);
        if(rateList != null) {
            for (RateLimit rl : rateList) {
                rules.put(rl.key, LimitRule.of(Duration.ofSeconds(rl.duration_sec), rl.limit, Duration.ofSeconds(rl.pause_sec)));
            }
        }
        return rules;
    }
}
