package com.agoda.rate.config;

import com.agoda.rate.utils.limitter.LimitRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = RateLimitConfig.class)
class RateLimitConfigTest {

    @Autowired
    private RateLimitConfig limitConfig;

    @Test
    public void getRateConfigTest() {
        assertThat(limitConfig.getRateConfig("hotels"))
                .containsOnlyKeys("room", "city", "default");

    }

    @Test
    public void getRateConfigTestWithData() {
        Map<String, LimitRule> hotel = limitConfig.getRateConfig("hotels");
        LimitRule expectedRule =LimitRule.of(Duration.ofSeconds(5),10,Duration.ofSeconds(5));
        LimitRule city = hotel.get("city");
        assertEquals(expectedRule.getLimit(),city.getLimit());
        assertEquals(expectedRule.getDurationSeconds(),city.getDurationSeconds());
        assertThat(limitConfig.getRateConfig("hotels"))
                .containsOnlyKeys("room", "city", "default");

    }

}