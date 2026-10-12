package com.skyisyours.service;

import com.skyisyours.payload.BookingRequestDTO;
import com.skyisyours.payload.BookingResponseDTO;

public interface BookingService{
    BookingResponseDTO createBooking(BookingRequestDTO bookingDTO);
    BookingResponseDTO getBookingById(Long bookingId);
    BookingResponseDTO deleteBooking(Long bookingId);
    BookingResponseDTO adjustSeats(Long bookingId, int newSeatCount);
}
