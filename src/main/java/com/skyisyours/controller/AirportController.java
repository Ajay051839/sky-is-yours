package com.skyisyours.controller;

import com.skyisyours.model.Airport;
import com.skyisyours.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/api")
public class AirportController {
    private AirportService airportService;
    public AirportController (AirportService airportService)
    {
        this.airportService = airportService;
    }

    @RequestMapping(value = "/airport", method = RequestMethod.POST)
    public Airport AddAirport (@RequestBody Airport airport)
    {
        return (airportService.addAirport(airport));
    }

    @RequestMapping(value = "/airport/{airportCode}", method = RequestMethod.DELETE)
    public Airport DeleteAirport (@PathVariable String airportCode)
    {
        return (airportService.deleteAirport(airportCode));

    }
}
