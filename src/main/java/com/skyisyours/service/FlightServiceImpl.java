package com.skyisyours.service;

import com.skyisyours.model.Flight;
import com.skyisyours.exceptions.APIException;
import com.skyisyours.exceptions.ResourceNotFoundException;
import com.skyisyours.payload.FlightDTO;
import com.skyisyours.payload.FlightResponseDTO;
import com.skyisyours.repository.FlightRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightServiceImpl implements FlightService {

    private FlightRepository flightRepository;
    private ModelMapper modelMapper;

    @Autowired
    public FlightServiceImpl(FlightRepository flightRepository, ModelMapper modelMapper) {
        this.flightRepository = flightRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public FlightResponseDTO addFlight(FlightDTO flightRequestDTO) {
        Flight flight = modelMapper.map(flightRequestDTO, Flight.class);
        if (!flightRequestDTO.getArrivalAt().isAfter(flightRequestDTO.getDepartureAt())) {
            throw new APIException("Arrival time must be strictly after departure time");
        }
        boolean flightExists = flightRepository
                .existsByFlightNumberAndDepartureAt(flight.getFlightNumber(), flight.getDepartureAt());

        if (flightExists) {
            throw new APIException("Flight " + flight.getFlightNumber() + " already scheduled for departure at " + flight.getDepartureAt());
        }
        Flight savedFlight = flightRepository.save(flight);
        return modelMapper.map(savedFlight, FlightResponseDTO.class);
    }

    @Override
    public FlightResponseDTO deleteFlight(Long flightId) {
        Flight flightToDelete = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "id", flightId));
        flightRepository.delete(flightToDelete);
        return modelMapper.map(flightToDelete, FlightResponseDTO.class);
    }

    @Override
    public FlightResponseDTO modifyFlight(FlightDTO flightRequestDTO, Long flightId) {
        Flight existingFlight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "id", flightId));
        existingFlight.setFlightNumber(flightRequestDTO.getFlightNumber());
        existingFlight.setOriginCode(flightRequestDTO.getOriginCode());
        existingFlight.setDestinationCode(flightRequestDTO.getDestinationCode());
        existingFlight.setDepartureAt(flightRequestDTO.getDepartureAt());
        existingFlight.setArrivalAt(flightRequestDTO.getArrivalAt());
        existingFlight.setTotalSeats(flightRequestDTO.getTotalSeats());
        existingFlight.setAvailableSeats(flightRequestDTO.getAvailableSeats());
        existingFlight.setBasePrice(flightRequestDTO.getBasePrice());
        existingFlight.setStatus(flightRequestDTO.getStatus());

        Flight savedFlight = flightRepository.save(existingFlight);
        return modelMapper.map(savedFlight, FlightResponseDTO.class);
    }

    @Override
    public List<FlightResponseDTO> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(flight -> modelMapper.map(flight, FlightResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public FlightResponseDTO getFlightById(Long flightId) {
        return flightRepository.findById(flightId).stream().map(flight -> modelMapper.map(flight, FlightResponseDTO.class)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "id", flightId));
    }

    @Override
    public List<FlightResponseDTO> searchFlights(String originCode, String destinationCode, LocalDateTime departureAt) {
        return flightRepository.findByOriginCodeAndDestinationCodeAndDepartureAtGreaterThanEqual(
                originCode, destinationCode, departureAt
        ).stream()
                .map(flight -> modelMapper.map(flight, FlightResponseDTO.class))
                .collect(Collectors.toList());
    }
}