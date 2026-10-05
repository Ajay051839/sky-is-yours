package com.skyisyours.controller;

import com.skyisyours.model.Flight;
import com.skyisyours.payload.FlightDTO;
import com.skyisyours.payload.FlightResponseDTO;
import com.skyisyours.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    // POST /api/flights - Create a new flight
    @PostMapping
    public ResponseEntity<FlightResponseDTO> createFlight(@Valid @RequestBody FlightDTO flightRequestDTO) {
        FlightResponseDTO createdFlight = flightService.addFlight(flightRequestDTO);
        return new ResponseEntity<>(createdFlight, HttpStatus.CREATED);
    }

    // GET /api/flights - Fetch all flights
    @GetMapping
    public ResponseEntity<List<FlightResponseDTO>> getAllFlights() {
        List<FlightResponseDTO> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }

    // GET /api/flights/{id} - Fetch flight by ID
    @GetMapping("/{id}")
    public ResponseEntity<FlightResponseDTO> getFlightById(@PathVariable Long id) {
        FlightResponseDTO flight = flightService.getFlightById(id);
        return ResponseEntity.ok(flight);
    }

    // GET /api/flights/search?origin=BLR&destination=DEL&departureAt=2026-10-15T08:00:00
    @GetMapping("/search")
    public ResponseEntity<List<FlightResponseDTO>> searchFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureAt) {

        List<FlightResponseDTO> flights = flightService.searchFlights(origin, destination, departureAt);
        return ResponseEntity.ok(flights);
    }

    //  Update an existing flight
    @PutMapping("/{id}")
    public ResponseEntity<FlightResponseDTO> updateFlight(@PathVariable Long id, @Valid@RequestBody FlightDTO flight) {
//        flight.setId(id);
        FlightResponseDTO updatedFlight = flightService.modifyFlight(flight,id);
        return ResponseEntity.ok(updatedFlight);
    }

    // DELETE /api/flights/{id} - Delete a flight
    @DeleteMapping("/{id}")
    public ResponseEntity<FlightResponseDTO> deleteFlight(@PathVariable Long id) {
        FlightResponseDTO deletedFlightResponseDTO = flightService.deleteFlight(id);
        return ResponseEntity.ok(deletedFlightResponseDTO);
    }
}