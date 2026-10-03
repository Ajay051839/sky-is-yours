package com.skyisyours.service;

import com.skyisyours.model.Flight;
import com.skyisyours.exceptions.APIException;
import com.skyisyours.exceptions.ResourceNotFoundException;
import com.skyisyours.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Override
    public Flight addFlight(Flight flight) {
        boolean flightExists = flightRepository
                .existsByFlightNumberAndDepartureAt(flight.getFlightNumber(), flight.getDepartureAt());

        if (flightExists) {
            throw new APIException("Flight " + flight.getFlightNumber() + " already scheduled for departure at " + flight.getDepartureAt());
        }

        return flightRepository.save(flight);
    }

    @Override
    public Flight deleteFlight(Long flightId) {
        Flight flightToDelete = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "id", flightId));

        flightRepository.delete(flightToDelete);
        return flightToDelete;
    }

    @Override
    public Flight modifyFlight(Flight flight) {
        Flight existingFlight = flightRepository.findById(flight.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "id", flight.getId()));

        existingFlight.setFlightNumber(flight.getFlightNumber());
        existingFlight.setOriginCode(flight.getOriginCode());
        existingFlight.setDestinationCode(flight.getDestinationCode());
        existingFlight.setDepartureAt(flight.getDepartureAt());
        existingFlight.setArrivalAt(flight.getArrivalAt());
        existingFlight.setTotalSeats(flight.getTotalSeats());
        existingFlight.setAvailableSeats(flight.getAvailableSeats());
        existingFlight.setBasePrice(flight.getBasePrice());
        existingFlight.setStatus(flight.getStatus());

        return flightRepository.save(existingFlight);
    }

    @Override
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    @Override
    public Flight getFlightById(Long flightId) {
        return flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "id", flightId));
    }

    @Override
    public List<Flight> searchFlights(String originCode, String destinationCode, LocalDateTime departureAt) {
        return flightRepository.findByOriginCodeAndDestinationCodeAndDepartureAtGreaterThanEqual(
                originCode, destinationCode, departureAt
        );
    }
}