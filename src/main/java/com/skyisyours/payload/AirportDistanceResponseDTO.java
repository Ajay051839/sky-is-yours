package com.skyisyours.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirportDistanceResponseDTO {
    private String sourceAirportCode;
    private String destinationAirportCode;
    private Double distanceInKms;
    private Double distanceInMiles;
    private Integer estimatedFlightDurationInMinutes;
}
