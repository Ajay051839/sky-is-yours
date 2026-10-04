package com.skyisyours.controller;

import com.skyisyours.config.AppConstants;
import com.skyisyours.payload.AirportDTO;
import com.skyisyours.payload.AirportDistanceResponseDTO;
import com.skyisyours.payload.AirportResponse;
import com.skyisyours.service.AirportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping ("/api/airports")
public class AirportController {
    private final AirportService airportService;

    // Creates a new airport record with validated request body payload
    @PostMapping
    public ResponseEntity<AirportDTO> addAirport (@Valid @RequestBody AirportDTO airport)
    {
        AirportDTO addedAirport = airportService.addAirport(airport);
        return new ResponseEntity<> (addedAirport,HttpStatus.CREATED);
    }

    // Fetches a paginated and sorted list of all airports using system default constants
    @GetMapping
    public ResponseEntity<AirportResponse> getAirports(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                       @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                       @RequestParam(name = "sortBy", defaultValue = AppConstants.AIRPORT_SORT_BY, required = false) String sortBy,
                                                       @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_BY, required = false) String sortOrder){

        return new ResponseEntity<>(airportService.getAllAirports(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    // Deletes an airport by its database ID
    @DeleteMapping(value = "/{airportId}")
    public ResponseEntity<AirportDTO> deleteAirport (@Valid @PathVariable Long airportId)
    {
        AirportDTO deletedAirport = airportService.deleteAirport(airportId);
        return new ResponseEntity<> (deletedAirport, HttpStatus.OK);
    }

    // Partially updates existing airport details for a specific ID
    @PutMapping(value = "/{airportId}")
    public ResponseEntity<AirportDTO> modifyAirport (@Valid @RequestBody AirportDTO airport,
                                                     @Valid @PathVariable Long airportId)
    {
        AirportDTO modifiedAirport = airportService.modifyAirport(airport, airportId);
        return new ResponseEntity<> (modifiedAirport,HttpStatus.CREATED);
    }

    // Searches airports matching substring, country code, and active status filters with pagination
    @GetMapping(value = "/search")
    public ResponseEntity<AirportResponse> filterAirportsByCountry(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                   @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                   @RequestParam(name = "sortBy", defaultValue = AppConstants.AIRPORT_SORT_BY, required = false) String sortBy,
                                                                   @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_BY, required = false) String sortOrder,
                                                                   @RequestParam(name = "countryCode", required = false) String countryCode,
                                                                   @RequestParam(name = "searchStr", required = false) String searchStr,
                                                                   @RequestParam(name = "isActive", required = false) Boolean isActive){

        return new ResponseEntity<>(airportService.getAirportsBySubstringAndCountyAndIsActive(searchStr, countryCode, isActive, pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    // Sets airport active flag to true
    @PatchMapping(value = "/{id}/activate")
    public ResponseEntity<AirportDTO> activateAirport(@PathVariable Long id)
    {
        return new ResponseEntity<>(airportService.activateOrDeactivateAirport(id, true), HttpStatus.OK);
    }

    // Sets airport active flag to false
    @PatchMapping(value = "/{id}/deactivate")
    public ResponseEntity<AirportDTO> deactivateAirport(@PathVariable Long id)
    {
        return new ResponseEntity<>(airportService.activateOrDeactivateAirport(id, false), HttpStatus.OK);
    }

    // Computes flight distance and duration between two 3-letter IATA codes using Haversine formula
    @GetMapping("/distance")
    public ResponseEntity<AirportDistanceResponseDTO> calculateDistance(
            @RequestParam @Pattern(regexp = "^[A-Z]{3}$", message = "Uppercase 3 Alphabets accepted only") String origin,
            @RequestParam @Pattern(regexp = "^[A-Z]{3}$", message = "Uppercase 3 Alphabets accepted only") String destination) {

        AirportDistanceResponseDTO response = airportService.calculateDistance(origin, destination);
        return ResponseEntity.ok(response);
    }
}