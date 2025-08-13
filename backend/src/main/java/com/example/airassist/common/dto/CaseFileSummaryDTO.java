package com.example.airassist.common.dto;

import com.example.airassist.common.enums.CaseStatus;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
public class CaseFileSummaryDTO {
    private UUID caseId;
    private String contractId;
    private Timestamp caseDate;
    private String flightNr;
    private Timestamp flightDepartureDate;
    private Timestamp flightArrivalDate;
    private String reservationNumber;
    private String passengerName;
    private CaseStatus status;
    private String colleague;
}
