package com.agoda.rate.dto;

import com.agoda.rate.model.Hotel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HotelDaoImpl implements HotelDao{
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Hotel> findByCity(String city) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("city", city);

        String sql = "SELECT * FROM hotels WHERE city=:city";
        List<Hotel> result = namedParameterJdbcTemplate.query(
                sql,
                params,
                new HotelMapper());


        return result;
    }

    @Override
    public List<Hotel> findByRoom(String room) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("room", room);

        String sql = "SELECT * FROM hotels WHERE room=:room";
        List<Hotel> result = namedParameterJdbcTemplate.query(
                sql,
                params,
                new HotelMapper());


        return result;
    }

    private static final class HotelMapper implements RowMapper<Hotel> {

        public Hotel mapRow(ResultSet rs, int rowNum) throws SQLException {
            Hotel hotel = new Hotel();
            hotel.setHotelID(rs.getInt("HOTELID"));
            hotel.setCity(rs.getString("CITY"));
            hotel.setRoom(rs.getString("ROOM"));
            hotel.setPrice(rs.getInt("PRICE"));
            return hotel;
        }
    }

}
