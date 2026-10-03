package com.skyisyours.service;

import com.skyisyours.model.Airport;
import com.skyisyours.payload.AirportDTO;
import com.skyisyours.payload.AirportDistanceResponseDTO;
import com.skyisyours.payload.AirportResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface AirportService {
    AirportDTO addAirport(AirportDTO airportDTO);

    AirportDTO deleteAirport(Long airportId);

    AirportDTO modifyAirport(AirportDTO airportDTO, Long id);

    AirportResponse getAllAirports(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    AirportResponse getAirportsBySubstringAndCountyAndIsActive(String searchStr, String countryCode, Boolean isActive, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    AirportDTO activateOrDeactivateAirport(@Valid Long id, boolean b);

    AirportDistanceResponseDTO calculateDistance(@NotBlank @Pattern(regexp = "^[A-Z]{3}$") String origin, @NotBlank @Pattern(regexp = "^[A-Z]{3}$") String destination);
}
