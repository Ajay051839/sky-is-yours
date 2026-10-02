package com.skyisyours.service;

import com.skyisyours.model.Airport;
import com.skyisyours.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportServiceImpl implements AirportService{

    @Autowired
    private AirportRepository airportRepository;

    @Override
    public Airport addAirport(Airport airportObject) {
        airportRepository.save(airportObject);
        return airportObject;
    }

    @Override
    public Airport deleteAirport(String airportCode) {
        Airport airportToDelete = airportRepository.getReferenceById(airportCode);
        airportRepository.delete(airportToDelete);
        return airportToDelete;
    }

    @Override
    public Airport modifyAirport(Airport airportObject) {
        return null;
    }

    @Override
    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }
}
