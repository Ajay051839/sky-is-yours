package com.skyisyours.service;

import com.skyisyours.config.AppConstants;
import com.skyisyours.exceptions.APIException;
import com.skyisyours.exceptions.ResourceNotFoundException;
import com.skyisyours.model.Airport;
import com.skyisyours.payload.AirportDTO;
import com.skyisyours.payload.AirportDistanceResponseDTO;
import com.skyisyours.payload.AirportResponse;
import com.skyisyours.repository.AirportRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.*;

@Service
public class AirportServiceImpl implements AirportService{

    private final AirportRepository airportRepository;
    private final ModelMapper modelMapper;

    // Constructor Injection
    public AirportServiceImpl(AirportRepository airportRepository, @Qualifier("ModelMapperWithSkipNull") ModelMapper modelMapper)
    {
        this.airportRepository = airportRepository;
        this.modelMapper = modelMapper;
    }

    // Creates and persists a new airport record
    @Override
    public AirportDTO addAirport(AirportDTO airportDTO)
    {
        Airport airportToBeAdded = modelMapper.map(airportDTO, Airport.class);
        Airport addedAirport = airportRepository.save(airportToBeAdded);
        return modelMapper.map(addedAirport, AirportDTO.class);
    }

    // Removes an airport record by ID after verifying existence
    @Override
    public AirportDTO deleteAirport(Long airportId) {
        Optional<Airport> optionalAirportToDelete = airportRepository.findById(airportId);
        Airport airportToDelete = optionalAirportToDelete.orElseThrow(() -> new ResourceNotFoundException("Airport", "id", airportId));
        airportRepository.delete(airportToDelete);
        AirportDTO deletedAirportDTO = modelMapper.map(airportToDelete, AirportDTO.class);
        return deletedAirportDTO;
    }

    // Performs partial update by copying non-null DTO fields onto the existing entity
    @Override
    public AirportDTO modifyAirport(AirportDTO airportDTO, Long id) {
        Optional<Airport> optionalAirportToModify = airportRepository.findById(id);
        Airport airportToModify = optionalAirportToModify.orElseThrow(() -> new ResourceNotFoundException("Airport", "id", id));
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(airportDTO, airportToModify);
        airportRepository.save(airportToModify);
        return modelMapper.map(airportToModify, AirportDTO.class);
    }

    // Fetches paginated and sorted list of all airports
    @Override
    public AirportResponse getAllAirports(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Airport> airportPageableList = airportRepository.findAll(pageable);
        List<AirportDTO> airportListDTO = airportPageableList.stream()
                .map(airport -> modelMapper.map(airport, AirportDTO.class))
                .collect(Collectors.toList());
        AirportResponse airportResponse = new AirportResponse();
        airportResponse.setContent(airportListDTO);
        setAirportPageableParams(airportPageableList, airportResponse);
        return airportResponse;
    }

    // Filters airports by substring match, country code, and active status with pagination
    @Override
    public AirportResponse getAirportsBySubstringAndCountyAndIsActive(String searchStr, String countryCode, Boolean isActive, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Airport> airportPageableList = airportRepository.findBySubstringAndCountryCodeAndIsActive(searchStr, countryCode, isActive, pageable);
        List<AirportDTO> airportListDTO = airportPageableList.stream()
                .map(airport -> modelMapper.map(airport, AirportDTO.class))
                .collect(Collectors.toList());
        AirportResponse airportResponse = new AirportResponse();
        airportResponse.setContent(airportListDTO);
        setAirportPageableParams(airportPageableList, airportResponse);
        return airportResponse;

    }

    // Toggles airport active status with validation against redundant state changes
    @Override
    public AirportDTO activateOrDeactivateAirport(Long id, boolean b) {
        Optional<Airport> optionalAirportToUpdate = airportRepository.findById(id);
        Airport airportToUpdate = optionalAirportToUpdate.orElseThrow(() -> new ResourceNotFoundException("Airport", "id", id));
        if(airportToUpdate.getIsActive() != null && airportToUpdate.getIsActive().equals(b))
            throw new APIException("Airport activity status is already "+b);
        airportToUpdate.setIsActive(b);
        Airport updatedAirport = airportRepository.save(airportToUpdate);
        return modelMapper.map(updatedAirport, AirportDTO.class);
    }

    // Calculates great-circle distance between two airports using the Haversine formula
    @Override
    public AirportDistanceResponseDTO calculateDistance(String origin, String destination) {
        String originTrimmed = origin.toUpperCase().trim();
        String destinationTrimmed = destination.toUpperCase().trim();
        if(originTrimmed.equals(destinationTrimmed))
            throw new APIException("Source and Destination provided are same: "+originTrimmed);
        Optional<Airport> optionalSourceAirport = airportRepository.findByAirportCode(originTrimmed);
        Optional<Airport> optionalDestinationAirport = airportRepository.findByAirportCode(destinationTrimmed);
        Airport sourceAirport = optionalSourceAirport.orElseThrow(() -> new ResourceNotFoundException("Airport", "airportCode", originTrimmed));
        Airport destinationAirport = optionalDestinationAirport.orElseThrow(() -> new ResourceNotFoundException("Airport", "airportCode", destinationTrimmed));

        // Convert latitude and longitude differences to radians
        double differenceLatitude = Math.toRadians(destinationAirport.getLatitude() - sourceAirport.getLatitude());
        double differenceLongitude = Math.toRadians(destinationAirport.getLongitude() - sourceAirport.getLongitude());
        double sourceLatitudeInRadian = Math.toRadians(sourceAirport.getLatitude());
        double destinationLatitudeInRadian = Math.toRadians(destinationAirport.getLatitude());

        // Haversine 'a' component: square of half the chord length between the points
        double a = Math.pow(Math.sin(differenceLatitude / 2), 2) + Math.pow(Math.sin(differenceLongitude / 2), 2) *
                Math.cos(sourceLatitudeInRadian) * Math.cos(destinationLatitudeInRadian);
        AirportDistanceResponseDTO airportDistanceResponseDTO = new AirportDistanceResponseDTO();

        // Compute distance in km rounded to two decimal places
        airportDistanceResponseDTO.setDistanceInKms(Math.round(2 * Math.asin(Math.sqrt(a))
                * AppConstants.EARTH_RADIUS * 100.00)/100.00);
        airportDistanceResponseDTO.setSourceAirportCode(sourceAirport.getAirportCode());
        airportDistanceResponseDTO.setDestinationAirportCode(destinationAirport.getAirportCode());

        // Convert km to miles and estimate flight duration
        airportDistanceResponseDTO.setDistanceInMiles(Math.round((airportDistanceResponseDTO.getDistanceInKms()
                /AppConstants.KM_TO_MILES_MULTIPLE) * 100.00)/100.00);
        airportDistanceResponseDTO.setEstimatedFlightDurationInMinutes(
                (int) (airportDistanceResponseDTO.getDistanceInKms() / AppConstants.AIRPLANE_AVG_SPEED_PER_MIN));
        return airportDistanceResponseDTO;
    }

    // Maps pagination metadata from Spring Data Page to response wrapper
    public void setAirportPageableParams(Page<Airport> airportPage, AirportResponse airportResponse)
    {
        airportResponse.setPageNumber(airportPage.getNumber());
        airportResponse.setPageSize(airportPage.getSize());
        airportResponse.setTotalElements(airportPage.getTotalElements());
        airportResponse.setTotalPages(airportPage.getTotalPages());
        airportResponse.setLastPage(airportPage.isLast());
    }
}