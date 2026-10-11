package com.skyisyours.controller;

import com.skyisyours.payload.BookingRequestDTO;
import com.skyisyours.payload.BookingResponseDTO;
import com.skyisyours.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create")
    public BookingResponseDTO createBooking(@Valid @RequestBody BookingRequestDTO bookingDTO) {
        return bookingService.createBooking(bookingDTO);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDTO getBooking(@Valid @PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @PutMapping("/adjust/{bookingId}")
    public BookingResponseDTO adjustSeats(@Valid @PathVariable Long bookingId, @RequestBody BookingRequestDTO bookingDTO) {
        return bookingService.adjustSeats(bookingId, bookingDTO.getNumberOfSeats());
    }

    @DeleteMapping("/delete/{bookingId}")
    public BookingResponseDTO deleteBooking(@Valid @PathVariable Long bookingId) {
        return bookingService.deleteBooking(bookingId);
    }
}
