package com.skyisyours.payload;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirportDTO {
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{3}$", message = "Uppercase alphabets only accepted")
    private String airportCode;

    @NotBlank
    private String airportName;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{4}$", message = "Uppercase alphabets of length 4 only accepted")
    private String icaoCode;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String country;

    @NotBlank
    private String timezone;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Float latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Float longitude;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}$", message = "Uppercase alphabets of length 2 only accepted")
    private String countryCode;

    @NotNull(message = "Active status is required")
    private Boolean isActive;
}
