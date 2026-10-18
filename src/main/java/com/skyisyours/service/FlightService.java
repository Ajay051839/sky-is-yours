package com.skyisyours.service;

import com.skyisyours.model.Flight;
import com.skyisyours.payload.FlightDTO;
import com.skyisyours.payload.FlightResponse;
import com.skyisyours.payload.FlightResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FlightService {
    FlightResponseDTO addFlight(FlightDTO flightRequestDTO);
    FlightResponseDTO deleteFlight(Long flightId);
    FlightResponseDTO modifyFlight(FlightDTO flightRequestDTO, Long flightId);
    FlightResponse getAllFlights(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    FlightResponseDTO getFlightById(Long flightId);
    List<FlightResponseDTO> searchFlights(String origin, String destination, LocalDate date, Integer year, Integer month);
}