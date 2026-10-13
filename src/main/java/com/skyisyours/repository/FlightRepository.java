package com.skyisyours.repository;

import com.skyisyours.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight,Long>, JpaSpecificationExecutor<Flight> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Flight f SET f.availableSeats = f.availableSeats - :seats " +
            "WHERE f.id IN :bookindIds AND f.availableSeats >= :seats")
    int decrementSeats(@Param("bookindIds") List<Long> bookingIds,
                       @Param("seats") Integer seats);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Flight f SET f.availableSeats = f.availableSeats + :seats " +
            "WHERE f.id IN :bookindIds AND f.availableSeats >= :seats")
    int incrementSeats(@Param("bookindIds") List<Long> bookingIds,
                       @Param("seats") Integer seats);
}
