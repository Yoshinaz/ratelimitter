package com.agoda.rate.service.impl;

import com.agoda.rate.dto.HotelDto;
import com.agoda.rate.entity.Hotel;
import com.agoda.rate.repository.HotelRepository;
import com.agoda.rate.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    HotelRepository hotelRepository;

    public List<Hotel> loadCity(String city){
        return hotelRepository.findByCity(city);
    }

    public List<Hotel> loadRoom(String room){
        return hotelRepository.findByRoom(room);
    }
}
