package com.skyisyours.specification;

import com.skyisyours.model.Flight;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class FlightSpecifications {

    public static Specification<Flight> hasOrigin(String origin) {
        return (root, query, cb) ->
                (origin == null || origin.trim().isEmpty()) ? cb.conjunction() : cb.equal(root.get("originCode"), origin);
    }

    public static Specification<Flight> hasDestination(String destination) {
        return (root, query, cb) ->
                (destination == null || destination.trim().isEmpty()) ? cb.conjunction() : cb.equal(root.get("destinationCode"), destination);
    }

    public static Specification<Flight> departureBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) return cb.conjunction();
            if (startDate != null && endDate != null) {
                return cb.between(root.get("departureAt"), startDate, endDate);
            } else if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("departureAt"), startDate);
            } else {
                return cb.lessThanOrEqualTo(root.get("departureAt"), endDate);
            }
        };
    }
}