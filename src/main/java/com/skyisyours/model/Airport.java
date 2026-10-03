package com.skyisyours.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 3)
    private String airportCode;

    @Column(unique = true)
    private String airportName;

    @Column(unique = true, length = 4)
    private String icaoCode;

    private String city;

    private String state;

    private String country;

    private String timezone;

    private Float latitude;

    private Float longitude;

    @Column(length = 2)
    private String countryCode;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isActive = false;
}
