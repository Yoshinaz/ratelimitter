package com.agoda.rate.controller;

import com.agoda.rate.config.RateLimitConfig;
import com.agoda.rate.dto.HotelDto;
import com.agoda.rate.entity.Hotel;
import com.agoda.rate.service.HotelService;
import com.agoda.rate.utils.limitter.LimitRule;
import com.agoda.rate.utils.limitter.RateLimiter;
import com.agoda.rate.utils.limitter.SlidingWindowRateLimit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = {"/api/v1/hotels"}, produces = APPLICATION_JSON_VALUE)
public class HotelController {
    private static final Logger logger = LoggerFactory.getLogger(HotelController.class);
    private final HotelService hotelService;
    private final RateLimiter rateLimit;


    @Autowired
    public HotelController(HotelService hotelService,RateLimitConfig limitConfig) {
        this.hotelService = hotelService;

        Map<String, LimitRule> rules = limitConfig.getRateConfig("hotels");//loadRateLimit();
        this.rateLimit = new SlidingWindowRateLimit(rules);
    }

    private Map<String, LimitRule> loadRateLimit(){
        Map<String, LimitRule> rules = new HashMap<>();
        rules.put("city",LimitRule.of(Duration.ofSeconds(5),2,Duration.ofSeconds(5)));
        rules.put("room",LimitRule.of(Duration.ofSeconds(10),100,Duration.ofSeconds(5)));
        return rules;
    }

    @Operation(summary = "Get hotels by city")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found the order", content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = HotelDto.class))}),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)})
    @GetMapping(path = "/city/{city}")
    public ResponseEntity<List<Hotel>> loadCity(@PathVariable(value = "city") String city) {
        if(rateLimit.isOverLimit("city")){
            return ResponseEntity.status(429).build();
        }else {
            final List<Hotel> hotels = hotelService.loadCity(city);
            if (hotels.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(hotels);
        }
    }

    @Operation(summary = "Get hotels by city")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found the order", content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = HotelDto.class))}),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)})
    @GetMapping(path = "/room/{room}")
    public ResponseEntity<List<Hotel>> loadRoom(@PathVariable(value = "room") String room) {
        if(rateLimit.isOverLimit("room")){
            return ResponseEntity.status(429).build();
        }else {
            final List<Hotel> hotels = hotelService.loadRoom(room);
            if (hotels.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(hotels);
        }
    }
}
