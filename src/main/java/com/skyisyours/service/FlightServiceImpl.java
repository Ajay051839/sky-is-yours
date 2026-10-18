package com.skyisyours.service;

import com.skyisyours.model.Flight;
import com.skyisyours.exceptions.APIException;
import com.skyisyours.exceptions.ResourceNotFoundException;
import com.skyisyours.payload.FlightDTO;
import com.skyisyours.payload.FlightResponse;
import com.skyisyours.payload.FlightResponseDTO;
import com.skyisyours.repository.FlightRepository;
import com.skyisyours.specification.FlightSpecifications;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
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
        // Validate arrival is after departure first
        if (flightRequestDTO.getArrivalAt() == null || flightRequestDTO.getDepartureAt() == null ||
                !flightRequestDTO.getArrivalAt().isAfter(flightRequestDTO.getDepartureAt())) {
            throw new APIException("Arrival time must be strictly after departure time");
        }
        Flight flight = modelMapper.map(flightRequestDTO, Flight.class);
        try {
            // Directly attempt to save. The database unique constraint will prevent duplicates safely.
            Flight savedFlight = flightRepository.save(flight);
            return modelMapper.map(savedFlight, FlightResponseDTO.class);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            // Catch database constraint violation and throw a clean APIException
            throw new APIException("Flight " + flight.getFlightNumber() +
                    " already scheduled for departure at " + flight.getDepartureAt());
        }
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
    public FlightResponse getAllFlights(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Flight> flightPage = flightRepository.findAll(pageDetails);
        List<FlightResponseDTO> flights = flightPage.getContent().stream()
                .map(flight -> modelMapper.map(flight, FlightResponseDTO.class))
                .collect(Collectors.toList());

        // Setting pagination metadata details in the response
        FlightResponse response = new FlightResponse();
        response.setContent(flights);
        response.setPageNumber(flightPage.getNumber());
        response.setPageSize(flightPage.getSize());
        response.setTotalElements(flightPage.getTotalElements());
        response.setTotalPages(flightPage.getTotalPages());
        response.setLastPage(flightPage.isLast());
        return response;
    }

    @Override
    public FlightResponseDTO getFlightById(Long flightId) {
        return flightRepository.findById(flightId).stream().map(flight -> modelMapper.map(flight, FlightResponseDTO.class)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Flight", "id", flightId));
    }

    @Override
    public List<FlightResponseDTO> searchFlights(String origin, String destination, LocalDate date, Integer year, Integer month) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if (date != null) {
            startDate = date.atStartOfDay();
            endDate = date.atTime(LocalTime.MAX);
        } else if (year != null && month != null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(1).atStartOfDay();
            endDate = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        }
        // Build dynamic specification query
        org.springframework.data.jpa.domain.Specification<Flight> spec = org.springframework.data.jpa.domain.Specification
                .where(FlightSpecifications.hasOrigin(origin))
                .and(FlightSpecifications.hasDestination(destination))
                .and(FlightSpecifications.departureBetween(startDate, endDate));

        List<Flight> flights = flightRepository.findAll(spec);

        return flights.stream()
                .map(flight -> modelMapper.map(flight, FlightResponseDTO.class))
                .collect(Collectors.toList());
    }

}