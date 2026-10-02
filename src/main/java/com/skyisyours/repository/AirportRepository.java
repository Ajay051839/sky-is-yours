package com.skyisyours.repository;

import com.skyisyours.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    Optional<Airport> findByAirportCode(String airportCode);
    Optional<Airport> findByAirportName(String airportName);
    Optional<Airport> findByIcaoCode(String icaoCode);
}
