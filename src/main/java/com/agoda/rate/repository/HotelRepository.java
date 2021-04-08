package com.agoda.rate.repository;

import com.agoda.rate.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,String> {

    @Query(value = "SELECT * FROM hotels WHERE room = :room",nativeQuery = true)
    List<Hotel> findByRoom(@Param("room") String room);

    @Query(value = "SELECT * FROM hotels WHERE city = :city",nativeQuery = true)
    List<Hotel> findByCity(@Param("city") String city);
}
