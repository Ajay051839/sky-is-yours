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
        airportResponse.setPageNumber(airportPageableList.getNumber());
        airportResponse.setPageSize(airportPageableList.getSize());
        airportResponse.setTotalElements(airportPageableList.getTotalElements());
        airportResponse.setTotalPages(airportPageableList.getTotalPages());
        airportResponse.setLastPage(airportPageableList.isLast());
        return airportResponse;
    }
}
