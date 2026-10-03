package com.skyisyours.service;

import com.skyisyours.model.Flight;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightService {
    Flight addFlight(Flight flight);
    Flight deleteFlight(Long flightId);
    Flight modifyFlight(Flight flight);
    List<Flight> getAllFlights();
    Flight getFlightById(Long flightId);
    List<Flight> searchFlights(String originCode, String destinationCode, LocalDateTime departureAt);
}