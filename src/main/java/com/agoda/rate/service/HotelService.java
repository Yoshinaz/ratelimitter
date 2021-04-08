package com.agoda.rate.service;

import com.agoda.rate.dto.HotelDto;
import com.agoda.rate.entity.Hotel;

import java.util.List;

public interface HotelService {
    public List<Hotel> loadCity(String city);
    public List<Hotel> loadRoom(String room);
}
