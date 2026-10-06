package com.skyisyours.payload;

import com.skyisyours.model.FlightStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class FlightDTO {

    @NotBlank(message = "Flight number is required")
    @Size(min = 2, max = 10, message = "Flight number must be between 2 and 10 characters")
    private String flightNumber;

    @NotBlank(message = "Origin code is required")
    @Size(min = 3, max = 3, message = "Origin code must be exactly 3 characters (e.g., BLR)")
    private String originCode;

    @NotBlank(message = "Destination code is required")
    @Size(min = 3, max = 3, message = "Destination code must be exactly 3 characters (e.g., DEL)")
    private String destinationCode;

    @NotNull(message = "Departure time is required")
    @Future(message = "Departure time must be in the future")
    private LocalDateTime departureAt;

    @NotNull(message = "Arrival time is required")
    @Future(message = "Arrival time must be in the future")
    private LocalDateTime arrivalAt;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats;

    @NotNull(message = "Available seats is required")
    @Min(value = 0, message = "Available seats cannot be negative")
    private Integer availableSeats;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than zero")
    private BigDecimal basePrice;

    @NotNull(message = "Status is required")
    private FlightStatus status;
}