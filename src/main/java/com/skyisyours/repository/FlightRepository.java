package com.skyisyours.repository;

import com.skyisyours.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight,Long> {
    // Spring Data JPA automatically generates the SQL count query from this method name
    boolean existsByFlightNumberAndDepartureAt(String flightNumber, LocalDateTime departureAt);

    // Custom finder for flight search
    List<Flight> findByOriginCodeAndDestinationCodeAndDepartureAtGreaterThanEqual(
            String originCode,
            String destinationCode,
            LocalDateTime departureAt
    );

}
