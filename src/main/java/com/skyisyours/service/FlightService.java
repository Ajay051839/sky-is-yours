package com.skyisyours.service;

import com.skyisyours.model.Flight;
import com.skyisyours.payload.FlightDTO;
import com.skyisyours.payload.FlightResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightService {
    FlightResponseDTO addFlight(FlightDTO flightRequestDTO);
    FlightResponseDTO deleteFlight(Long flightId);
    FlightResponseDTO modifyFlight(FlightDTO flightRequestDTO, Long flightId);
    List<FlightResponseDTO> getAllFlights();
    FlightResponseDTO getFlightById(Long flightId);
    List<FlightResponseDTO> searchFlights(String originCode, String destinationCode, LocalDateTime departureAt);
}