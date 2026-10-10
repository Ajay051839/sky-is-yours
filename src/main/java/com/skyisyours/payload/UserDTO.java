package com.skyisyours.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    // Length already enforced in regex {3,20}, or explicitly with @Size
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "Password must contain at least one letter and one number")
    private String password;

    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format")
    private String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String role;

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must contain only letters")
    private String firstName;

    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must contain only letters")
    private String lastName;

    @Size(min = 8, max = 20, message = "Phone number must be between 8 and 20 characters")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Size(max = 150, message = "Address cannot exceed 150 characters")
    @Pattern(regexp = "^[A-Za-z0-9\\s,.-]+$", message = "Address can contain letters, numbers, spaces, commas, periods, and hyphens")
    private String address;

    @Size(max = 50, message = "City cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "City must contain only letters and spaces")
    private String city;

    @Size(max = 50, message = "State cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "State must contain only letters and spaces")
    private String state;

    @Size(min = 5, max = 10, message = "ZIP code must be between 5 and 10 characters")
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Invalid ZIP code format")
    private String zipCode;

    @Size(max = 50, message = "Country cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Country must contain only letters and spaces")
    private String country;
}