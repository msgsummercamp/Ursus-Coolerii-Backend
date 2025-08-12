package com.example.airassist.common.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CaseDetailsDTO {
    private UUID caseId;
    private String contractId;
    private String reservationNumber;
    private List<FlightDetailsDTO> flights;
    private PassengerDTO passenger;
    private List<DocumentDTO> documents;
    private List<CommentDTO> comments;
}