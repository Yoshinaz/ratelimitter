package com.agoda.rate.controller;

import com.agoda.rate.RateApplication;
import com.agoda.rate.config.RateLimitConfig;
import com.agoda.rate.constant.RateConstants;
import com.agoda.rate.utils.limitter.LimitRule;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import java.time.Duration;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RateApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HotelControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private RateLimitConfig limitConfig;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();


    @SneakyThrows
    @Test
    public void testCityEndPoint() {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        Map<String, LimitRule> rules = limitConfig.getRateConfig(RateConstants.HotelControl.toString());
        LimitRule cityLimit = rules.get(RateConstants.Hotel.City.toString());
        String expectedJson = "[{city:BangkokTest,hotelID:27,room:DeluxeTest,price:2300},{city:BangkokTest,hotelID:28,room:DeluxeTest,price:2300}]";
        for(int i =0;i<cityLimit.getLimit();i++) {
            ResponseEntity<String> response = restTemplate.exchange(
                    createURLWithPort("/api/v1/hotels/city/BangkokTest"),
                    HttpMethod.GET, entity, String.class);

            JSONAssert.assertEquals(expectedJson, response.getBody(), false);
        }

        //Too many request
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/api/v1/hotels/city/BangkokTest"),
                HttpMethod.GET, entity, String.class);
        HttpStatus expected = HttpStatus.TOO_MANY_REQUESTS;
        Assert.assertEquals(expected, response.getStatusCode());

        //Still too many request
        response = restTemplate.exchange(
                createURLWithPort("/api/v1/hotels/city/BangkokTest"),
                HttpMethod.GET, entity, String.class);
        expected = HttpStatus.TOO_MANY_REQUESTS;
        Assert.assertEquals(expected, response.getStatusCode());

        //Still too many request
        response = restTemplate.exchange(
                createURLWithPort("/api/v1/hotels/city/BangkokTest"),
                HttpMethod.GET, entity, String.class);
        expected = HttpStatus.TOO_MANY_REQUESTS;
        Assert.assertEquals(expected, response.getStatusCode());

        //city3 endpoint still use able
        response = restTemplate.exchange(
        createURLWithPort("/api/v1/hotels/city3/BangkokTest"),
        HttpMethod.GET, entity, String.class);
        JSONAssert.assertEquals(expectedJson, response.getBody(), false);

        int pauseTime = Math.max(cityLimit.getDurationSeconds(),cityLimit.getStopDurationSeconds());
        //pause xx seconds
        Thread.sleep(Duration.ofSeconds(pauseTime).toMillis()+100);

        // city reusable
        response = restTemplate.exchange(
                createURLWithPort("/api/v1/hotels/city/BangkokTest"),
                HttpMethod.GET, entity, String.class);
        expected = HttpStatus.OK;
        Assert.assertEquals(expected, response.getStatusCode());
        JSONAssert.assertEquals(expectedJson, response.getBody(), false);
    }


    @SneakyThrows
    @Test
    public void testRoomEndPoint() {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        Map<String, LimitRule> rules = limitConfig.getRateConfig(RateConstants.HotelControl.toString());
        LimitRule roomLimit = rules.get(RateConstants.Hotel.Room.toString());
        String expectedJson = "[{city:BangkokTest,hotelID:27,room:DeluxeTest,price:2300},{city:BangkokTest,hotelID:28,room:DeluxeTest,price:2300}]";
        for(int i =0;i<roomLimit.getLimit();i++) {
            ResponseEntity<String> response = restTemplate.exchange(
                    createURLWithPort("/api/v1/hotels/room/DeluxeTest"),
                    HttpMethod.GET, entity, String.class);

            JSONAssert.assertEquals(expectedJson, response.getBody(), false);
        }

        //Too many request
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/api/v1/hotels/room/DeluxeTest"),
                HttpMethod.GET, entity, String.class);
        HttpStatus expected = HttpStatus.TOO_MANY_REQUESTS;
        Assert.assertEquals(expected, response.getStatusCode());

        //Still too many request
        response = restTemplate.exchange(
                createURLWithPort("/api/v1/hotels/room/DeluxeTest"),
                HttpMethod.GET, entity, String.class);
        expected = HttpStatus.TOO_MANY_REQUESTS;
        Assert.assertEquals(expected, response.getStatusCode());

        //city3 endpoint still use able
        response = restTemplate.exchange(
                createURLWithPort("/api/v1/hotels/city3/BangkokTest"),
                HttpMethod.GET, entity, String.class);
        JSONAssert.assertEquals(expectedJson, response.getBody(), false);

        int pauseTime = Math.max(roomLimit.getDurationSeconds(),roomLimit.getStopDurationSeconds());
        //pause xx seconds
        Thread.sleep(Duration.ofSeconds(pauseTime).toMillis()+100);

        // room reusable
        response = restTemplate.exchange(
                createURLWithPort("/api/v1/hotels/room/DeluxeTest"),
                HttpMethod.GET, entity, String.class);
        expected = HttpStatus.OK;
        Assert.assertEquals(expected, response.getStatusCode());
        JSONAssert.assertEquals(expectedJson, response.getBody(), false);
    }


    @SneakyThrows
    @Test
    public void testOverLimitForNotConfigLimitEndpoint() {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        Map<String, LimitRule> rules = limitConfig.getRateConfig(RateConstants.HotelControl.toString());
        LimitRule defaultLimit = rules.get(RateConstants.Default.toString());
        for(int i =0;i<defaultLimit.getLimit();i++) {
            ResponseEntity<String> response = restTemplate.exchange(
                    createURLWithPort("/api/v1/hotels/city2/BangkokTest"),
                    HttpMethod.GET, entity, String.class);
        }
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/api/v1/hotels/city2/BangkokTest"),
                HttpMethod.GET, entity, String.class);
        HttpStatus expected = HttpStatus.TOO_MANY_REQUESTS;
        Assert.assertEquals(expected, response.getStatusCode());
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}