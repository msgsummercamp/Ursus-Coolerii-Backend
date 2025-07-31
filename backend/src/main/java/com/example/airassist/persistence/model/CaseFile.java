package com.example.airassist.persistence.model;

import com.example.airassist.common.CaseStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cases")
public class CaseFile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID caseId;

    @NotBlank(message = "The case should contain the reservation number")
    private String reservationNumber;

    private CaseStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name= "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    @JsonManagedReference
    @OneToMany(mappedBy = "caseFile")
    private List<CaseFlights> caseFlights;

    @OneToMany(mappedBy = "caseFile")
    @JsonManagedReference
    private List<Comment> comments;

    @OneToMany(mappedBy = "caseFile")
    @JsonManagedReference
    private List<Document> documents;
}
