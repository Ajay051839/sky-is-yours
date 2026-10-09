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

    @NotBlank
    @Size(min = 2, max = 10)
    private String flightNumber;

    @NotBlank
    @Size(min = 3, max = 3)
    private String originCode;

    @NotBlank
    @Size(min = 3, max = 3)
    private String destinationCode;

    @NotNull
    @Future
    private LocalDateTime departureAt;

    @NotNull
    @Future
    private LocalDateTime arrivalAt;

    @NotNull
    @Min(value = 1)
    private Integer totalSeats;

    @NotNull
    @Min(value = 0)
    private Integer availableSeats;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal basePrice;

    @NotNull
    private FlightStatus status;
}