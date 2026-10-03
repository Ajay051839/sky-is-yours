package com.skyisyours.controller;

import com.skyisyours.config.AppConstants;
import com.skyisyours.payload.AirportDTO;
import com.skyisyours.payload.AirportResponse;
import com.skyisyours.service.AirportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping ("/api")
public class AirportController {
    private final AirportService airportService;

    @RequestMapping(value = "/airport", method = RequestMethod.POST)
    public ResponseEntity<AirportDTO> addAirport (@Valid @RequestBody AirportDTO airport)
    {
        AirportDTO addedAirport = airportService.addAirport(airport);
        return new ResponseEntity<> (addedAirport,HttpStatus.CREATED);
    }

    @RequestMapping(value="/airports", method = RequestMethod.GET)
    public ResponseEntity<AirportResponse> getAirports(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                       @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                       @RequestParam(name = "sortBy", defaultValue = AppConstants.AIRPORT_SORT_BY, required = false) String sortBy,
                                                       @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_BY, required = false) String sortOrder){

        return new ResponseEntity<>(airportService.getAllAirports(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @RequestMapping(value = "/airport/{airportId}", method = RequestMethod.DELETE)
    public ResponseEntity<AirportDTO> deleteAirport (@Valid @PathVariable Long airportId)
    {
        AirportDTO deletedAirport = airportService.deleteAirport(airportId);
        return new ResponseEntity<> (deletedAirport, HttpStatus.OK);
    }
    @RequestMapping(value = "/airport/{airportId}", method = RequestMethod.PUT)
    public ResponseEntity<AirportDTO> modifyAirport (@Valid @RequestBody AirportDTO airport,
                                                     @Valid @PathVariable Long airportId)
    {
        AirportDTO modifiedAirport = airportService.modifyAirport(airport, airportId);
        return new ResponseEntity<> (modifiedAirport,HttpStatus.CREATED);
    }

}
