package com.skyisyours.service;

import com.skyisyours.model.Airport;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface AirportService {
    Airport addAirport(Airport airportObject);
    Airport deleteAirport(Long airportId);
    Airport modifyAirport(Airport airportObject);
    List<Airport> getAllAirports();
    Optional<Airport> findExistingAirport(Airport airport);
}
