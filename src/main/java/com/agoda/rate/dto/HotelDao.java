package com.agoda.rate.dto;


import com.agoda.rate.model.Hotel;

import java.util.List;

public interface HotelDao {
    List<Hotel> findByCity(String city);
    List<Hotel> findByRoom(String room);
}
