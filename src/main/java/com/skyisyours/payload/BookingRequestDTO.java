package com.skyisyours.payload;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotEmpty(message = "At least one flight ID is required")
    @UniqueElements(message = "Flight IDs must be unique")
    private List<@NotNull @Positive Long> flightIds;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "Must book at least 1 seat")
    private Integer numberOfSeats;
}