package com.skyisyours.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long bookingId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserDTO user;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<FlightDTO> flights;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "Must book at least 1 seat")
    private Integer numberOfSeats;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String bookingTimestamp;
}

