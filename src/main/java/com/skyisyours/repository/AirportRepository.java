package com.skyisyours.repository;

import com.skyisyours.model.Airport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    @Query("SELECT a FROM Airport a WHERE " +
            "(:countryCode IS NULL OR a.countryCode = :countryCode) AND " +
            "(:isActive IS NULL OR a.isActive = :isActive) AND " +
            "((:searchStr IS NULL) OR " +
            "(LOWER(a.country) LIKE CONCAT('%',LOWER(:searchStr),'%')) OR " +
            "(LOWER(a.city) LIKE CONCAT('%',LOWER(:searchStr),'%')) OR " +
            "(LOWER(a.state) LIKE CONCAT('%',LOWER(:searchStr),'%')) OR " +
            "(LOWER(a.airportCode) LIKE CONCAT('%', LOWER(:searchStr), '%')) OR " +
            "(LOWER(a.icaoCode) LIKE CONCAT('%', LOWER(:searchStr), '%')) OR " +
            "(LOWER(a.airportName) LIKE CONCAT('%',LOWER(:searchStr),'%')))")
    Page<Airport> findBySubstringAndCountryCodeAndIsActive(
            @Param("searchStr") String searchStr,
            @Param("countryCode") String countryCode,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    Optional<Airport> findByAirportCode(String airportCode);
}
