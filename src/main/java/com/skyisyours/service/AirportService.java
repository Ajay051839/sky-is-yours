package com.skyisyours.service;

import com.skyisyours.model.Airport;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AirportService {
    Airport addAirport(Airport airportObject);
    Airport deleteAirport(String airportCode);
    Airport modifyAirport(Airport airportObject);
    List<Airport> getAllAirports();
}
