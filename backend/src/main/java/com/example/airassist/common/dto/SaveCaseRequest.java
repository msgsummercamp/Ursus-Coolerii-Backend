package com.example.airassist.common.dto;

import com.example.airassist.persistence.model.DisruptionDetails;
import com.example.airassist.persistence.model.Passenger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveCaseRequest {
    private DisruptionDetails disruptionDetails;
    private String reservationNumber;
    private List<FlightSaveDTO> flights;
    private Passenger passenger;
    private String userEmail;

}
