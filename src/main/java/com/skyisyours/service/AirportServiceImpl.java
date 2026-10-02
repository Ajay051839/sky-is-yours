package com.skyisyours.service;

import com.skyisyours.exceptions.APIException;
import com.skyisyours.exceptions.ResourceNotFoundException;
import com.skyisyours.model.Airport;
import com.skyisyours.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AirportServiceImpl implements AirportService{

    @Autowired
    private AirportRepository airportRepository;

    @Override
    public Airport addAirport(Airport airportObject)
    {
        Optional<Airport> optionalAirport = findExistingAirport(airportObject);
        Airport airport = optionalAirport.orElse(null);
        if(airport != null)
            throw new APIException("Airport already present with either the same name, ICAO Code or AirportCode");
        airportRepository.save(airportObject);
        return airportObject;
    }

    @Override
    public Airport deleteAirport(Long airportId) {
        Optional<Airport> optionalAirportToDelete = airportRepository.findById(airportId);
        Airport airportToDelete = optionalAirportToDelete.orElseThrow(() -> new ResourceNotFoundException("Airport", "airportCode", airportId));
        airportRepository.delete(airportToDelete);
        return airportToDelete;
    }

    @Override
    public Airport modifyAirport(Airport airportObject) {
        Optional<Airport> optionalAirportToDelete = airportRepository.findById(airportObject.getId());
        Airport airportToModify = optionalAirportToDelete.orElseThrow(() -> new ResourceNotFoundException("Airport", "airportCode", airportObject.getAirportCode()));
        airportObject.setId(airportToModify.getId());
        airportRepository.save(airportObject);
        return airportObject;
    }

    @Override
    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    @Override
    public Optional<Airport> findExistingAirport(Airport airport) {
        Optional<Airport> currentAirport = airportRepository.findByAirportCode(airport.getAirportCode())
                .or(() -> airportRepository.findByAirportName(airport.getAirportName()))
                .or(() -> airportRepository.findByIcaoCode((airport.getIcaoCode())));
        return currentAirport;
    }


}
