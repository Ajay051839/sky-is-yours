package com.skyisyours.payload;

import com.skyisyours.model.FlightStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponseDTO {

    private Long id;
    private String flightNumber;
    private String originCode;
    private String destinationCode;
    private LocalDateTime departureAt;
    private LocalDateTime arrivalAt;
    private Integer totalSeats;
    private Integer availableSeats;
    private BigDecimal basePrice;
    private FlightStatus status;
    private LocalDateTime createdAt;
}