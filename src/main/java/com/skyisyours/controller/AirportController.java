package com.skyisyours.controller;

import com.skyisyours.model.Airport;
import com.skyisyours.service.AirportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api")
public class AirportController {
    private AirportService airportService;
    public AirportController (AirportService airportService)
    {
        this.airportService = airportService;
    }

    @RequestMapping(value = "/airport", method = RequestMethod.POST)
    public ResponseEntity<Airport> AddAirport (@Valid @RequestBody Airport airport)
    {
            Airport a = airportService.addAirport(airport);
            return new ResponseEntity<> (a,HttpStatus.CREATED);
    }

    @RequestMapping(value="/airports", method = RequestMethod.GET)
    public ResponseEntity<List<Airport>> GetAirports(){
        return new ResponseEntity<>(airportService.getAllAirports(), HttpStatus.OK);
    }

    @RequestMapping(value = "/airport/{airportId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> DeleteAirport (@Valid @PathVariable Long airportId)
    {
        Airport deletedAirport = airportService.deleteAirport(airportId);
        return new ResponseEntity<> ("Successfully deleted airport with code "+airportId, HttpStatus.OK);
    }
}
