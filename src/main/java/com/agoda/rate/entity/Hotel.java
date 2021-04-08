package com.agoda.rate.entity;

import lombok.*;
import org.springframework.context.annotation.Primary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "HOTELS")
public class Hotel {

    @Column(name = "CITY")
    private String city;

    @Id
    @Column(name = "HOTELID")
    private int hotelID;

    @Column(name = "ROOM")
    private String room;

    @Column(name = "PRICE")
    private int price;
}
