package com.skyisyours.service;

import com.skyisyours.exceptions.APIException;
import com.skyisyours.exceptions.ResourceNotFoundException;
import com.skyisyours.model.Booking;
import com.skyisyours.model.Flight;
import com.skyisyours.model.AppUser;
import com.skyisyours.payload.*;
import com.skyisyours.repository.BookingRepository;
import com.skyisyours.repository.FlightRepository;
import com.skyisyours.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final FlightService flightService;
    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    public BookingServiceImpl(BookingRepository bookingRepository, FlightRepository flightRepository,
                              UserRepository userRepository, @Qualifier("ModelMapperWithSkipNull") ModelMapper modelMapper,
                              FlightService flightService) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.flightService = flightService;
    }

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO bookingDTO) {
        // Validate the user
        AppUser user = userRepository.findById(bookingDTO.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User", "userId", bookingDTO.getUserId()));

        // Validate if all the flights exist
        List<Flight> flights = flightRepository.findAllById(bookingDTO.getFlightIds());
        // Extract all the found flights and map it to a Set for quick lookup
        Set<Long> foundFlightIds = flights.stream().map(Flight::getId).collect(Collectors.toSet());

        // Check for any flight IDs that were not found
        List<Long> missingFlightsIds = bookingDTO.getFlightIds().stream()
                .filter(flightId -> !foundFlightIds.contains(flightId))
                .toList();
        if (!missingFlightsIds.isEmpty()) {
            throw new ResourceNotFoundException("Flight", "flightId", missingFlightsIds);
        }
        // Update the available seats for each flight
        // Validate if the number of seats requested is available for each flight in the DB Query
        int updatedRows = flightRepository.decrementSeats(bookingDTO.getFlightIds(), bookingDTO.getNumberOfSeats());
        if(updatedRows < bookingDTO.getFlightIds().size()) {
            throw new APIException("Not enough available seats for one or more flights.");
        }
        flights.forEach(flight -> flight.setAvailableSeats(flight.getAvailableSeats() - bookingDTO.getNumberOfSeats()));

        // Add new booking to the database if every condition passes
        Booking bookingToBeAdded = new Booking();
        bookingToBeAdded.setFlights(flights);
        bookingToBeAdded.setUser(user);
        bookingToBeAdded.setNumberOfSeats(bookingDTO.getNumberOfSeats());

        Booking addedBooking = bookingRepository.save(bookingToBeAdded);

        return mapUserAndFlightsToBookingResponseDTO(addedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDTO getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking", "bookingId", bookingId));
        return mapUserAndFlightsToBookingResponseDTO(booking);
    }

    @Override
    @Transactional
    public BookingResponseDTO deleteBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking", "bookingId", bookingId));
        BookingResponseDTO deletedBookingDTO = mapUserAndFlightsToBookingResponseDTO(booking);
        List<Long> flightIdsToBeIncremented = booking.getFlights().stream().map(Flight::getId).toList();

        // Delete the booking from the database
        bookingRepository.delete(booking);
        // Increment the available seats for each flight in the booking
        flightRepository.incrementSeats(flightIdsToBeIncremented, booking.getNumberOfSeats());

        return deletedBookingDTO;
    }

    @Override
    @Transactional
    public BookingResponseDTO adjustSeats(Long bookingId, int newSeatCount) {
        // Check if the booking exists
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking", "bookingId", bookingId));
        // Update the number of seats for the booking
        int currentSeatCount = booking.getNumberOfSeats();
        int seatDifference = newSeatCount - currentSeatCount;
        if (seatDifference == 0)
            return mapUserAndFlightsToBookingResponseDTO(booking);
        List<Long> flightIds = booking.getFlights().stream().map(Flight::getId).toList();
        if (seatDifference > 0) {
            // Check if there are enough available seats in the flights
            int updatedRows = flightRepository.decrementSeats(flightIds, seatDifference);
            if (updatedRows < flightIds.size()) {
                throw new APIException("Not enough available seats for one or more flights.");
            }
            // Decrement the available seats in the flights
            booking.getFlights().forEach(flight -> flight.setAvailableSeats(flight.getAvailableSeats() - seatDifference));
        }
        else if (seatDifference < 0){
            // Increment the available seats in the flights
            flightRepository.incrementSeats(flightIds, -seatDifference);
            booking.getFlights().forEach(flight -> flight.setAvailableSeats(flight.getAvailableSeats() + (-seatDifference)));
        }
        booking.setNumberOfSeats(newSeatCount);
        bookingRepository.save(booking);
        return mapUserAndFlightsToBookingResponseDTO(booking);
    }

    private BookingResponseDTO mapUserAndFlightsToBookingResponseDTO(Booking booking) {
        List<FlightDTO> flightsDTO = booking.getFlights().stream()
                .map(flight -> modelMapper.map(flight, FlightDTO.class))
                .toList();
        UserDTO userDTO = modelMapper.map(booking.getUser(), UserDTO.class);
        BookingResponseDTO bookingResponseDTO = modelMapper.map(booking, BookingResponseDTO.class);
        bookingResponseDTO.setFlights(flightsDTO);
        bookingResponseDTO.setUser(userDTO);
        return bookingResponseDTO;
    }

}
