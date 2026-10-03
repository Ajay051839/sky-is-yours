package com.skyisyours.service;

import com.skyisyours.exceptions.APIException;
import com.skyisyours.exceptions.ResourceNotFoundException;
import com.skyisyours.model.Airport;
import com.skyisyours.payload.AirportDTO;
import com.skyisyours.payload.AirportResponse;
import com.skyisyours.repository.AirportRepository;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AirportServiceImpl implements AirportService{

    private final AirportRepository airportRepository;
    private final ModelMapper modelMapper;

    @Override
    public AirportDTO addAirport(AirportDTO airportDTO)
    {
        Airport airportToBeAdded = modelMapper.map(airportDTO, Airport.class);
        Airport addedAirport = airportRepository.save(airportToBeAdded);
        return modelMapper.map(addedAirport, AirportDTO.class);
    }

    @Override
    public AirportDTO deleteAirport(Long airportId) {
        Optional<Airport> optionalAirportToDelete = airportRepository.findById(airportId);
        Airport airportToDelete = optionalAirportToDelete.orElseThrow(() -> new ResourceNotFoundException("Airport", "id", airportId));
        airportRepository.delete(airportToDelete);
        AirportDTO deletedAirportDTO = modelMapper.map(airportToDelete, AirportDTO.class);
        return deletedAirportDTO;
    }

    @Override
    public AirportDTO modifyAirport(AirportDTO airportDTO, Long id) {
        Optional<Airport> optionalAirportToModify = airportRepository.findById(id);
        Airport airportToModify = optionalAirportToModify.orElseThrow(() -> new ResourceNotFoundException("Airport", "id", id));
        modelMapper.map(airportDTO, airportToModify);
        airportRepository.save(airportToModify);
        return modelMapper.map(airportToModify, AirportDTO.class);
    }

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

    public void setAirportPageableParams(Page<Airport> airportPage, AirportResponse airportResponse)
    {
        airportResponse.setPageNumber(airportPage.getNumber());
        airportResponse.setPageSize(airportPage.getSize());
        airportResponse.setTotalElements(airportPage.getTotalElements());
        airportResponse.setTotalPages(airportPage.getTotalPages());
        airportResponse.setLastPage(airportPage.isLast());
    }
}
