package com.example.airassist.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class CaseDetailsDTO {
    private String contractId;
    private String reservationNumber;
    private List<FlightDetailsDTO> flights;
    private PassengerDTO passenger;
    private List<DocumentDTO> documents;
    private List<CommentDTO> comments;
}