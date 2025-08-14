package com.example.airassist.util;

import com.example.airassist.common.dto.CaseDetailsDTO;
import com.example.airassist.common.dto.DocumentDTO;
import com.example.airassist.common.dto.FlightDetailsDTO;
import com.example.airassist.common.dto.PassengerDTO;
import com.example.airassist.persistence.model.CaseFile;
import com.example.airassist.persistence.model.CaseFlights;
import com.example.airassist.persistence.model.Passenger;

import java.util.List;
import java.util.stream.Collectors;

public class DtoUtils {

    public static FlightDetailsDTO getFlightDetailsDtoFromCaseFlight(CaseFlights caseFlights) {
        FlightDetailsDTO dto = new FlightDetailsDTO();
        dto.setFlightNumber(caseFlights.getFlight().getFlightNumber());
        dto.setAirline(caseFlights.getFlight().getAirline().getName());
        dto.setReservationNumber(caseFlights.getCaseFile().getReservationNumber());
        dto.setDepartureAirport(caseFlights.getFlight().getDepartureAirport());
        dto.setDestinationAirport(caseFlights.getFlight().getDestinationAirport());
        dto.setProblemFlight(caseFlights.isProblemFlight());
        dto.setPlannedDepartureTime(caseFlights.getFlight().getDepartureTime());
        dto.setPlannedArrivalTime(caseFlights.getFlight().getArrivalTime());
        dto.setFirstFlight(caseFlights.isFirst());
        dto.setLastFlight(caseFlights.isLast());
        return dto;
    }

    public static List<FlightDetailsDTO> getFlightDetailsDtoFromCaseflight(List<CaseFlights> caseFlights) {
        return caseFlights.stream().map(DtoUtils::getFlightDetailsDtoFromCaseFlight).collect(Collectors.toList());
    }

    public static List<FlightDetailsDTO> sortFlights(List<CaseFlights> caseFlights) {
        List<FlightDetailsDTO> dtos =  getFlightDetailsDtoFromCaseflight(caseFlights);
        List<FlightDetailsDTO> sorted = new java.util.ArrayList<>();
        FlightDetailsDTO current = dtos.stream().filter(FlightDetailsDTO::isFirstFlight).findFirst().orElse(null);
        if (current == null) return dtos;
        sorted.add(current);
        while (!current.isLastFlight()) {
            String nextDeparture = current.getDestinationAirport();
            FlightDetailsDTO next = dtos.stream()
                    .filter(f -> !sorted.contains(f) && f.getDepartureAirport().equals(nextDeparture))
                    .findFirst().orElse(null);
            if (next == null) break;
            sorted.add(next);
            current = next;
        }
        return sorted;
    }

    public static PassengerDTO  getPassengerFromCaseFile(CaseFile caseFile) {
        Passenger p = caseFile.getPassenger();
        PassengerDTO passengerDTO = new PassengerDTO();
        passengerDTO.setFirstName(p.getFirstName());
        passengerDTO.setLastName(p.getLastName());
        passengerDTO.setDateOfBirth(p.getDateOfBirth());
        passengerDTO.setPhone(p.getPhoneNumber());
        passengerDTO.setAddress(p.getAddress());
        passengerDTO.setPostalCode(p.getPostalCode());
        passengerDTO.setEmail(caseFile.getUser().getEmail());
        return passengerDTO;
    }

    public static CaseDetailsDTO getCaseDetailsDtoFromCaseFile(CaseFile caseFile){
        CaseDetailsDTO dto = new CaseDetailsDTO();
        dto.setCaseId(caseFile.getCaseId());
        dto.setContractId(caseFile.getContractId());
        dto.setReservationNumber(caseFile.getReservationNumber());
        dto.setFlights(sortFlights(caseFile.getCaseFlights()));

        PassengerDTO passengerDTO = getPassengerFromCaseFile(caseFile);
        dto.setPassenger(passengerDTO);

        dto.setDocuments(caseFile.getDocuments().stream().map(doc -> {
            DocumentDTO d = new DocumentDTO();
            d.setFilename(doc.getId().toString());
            d.setUploadTimestamp(caseFile.getCaseDate());
            return d;
        }).toList());
        return dto;
    }
}
