package com.example.airassist.persistence.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

enum Status {
    NOT_ASSIGNED,
    ASSIGNED,
    ELIGIBLE,
    NOT_ELIGIBLE,
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cases")
public class Case {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long caseId;

    @NotBlank(message = "The case should contain the reservation number")
    private String reservationNumber;

    private Status status;

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    @OneToMany(mappedBy = "caseFile")
    private List<CaseFlights> caseFlights;

    @OneToMany(mappedBy = "caseFile")
    private List<Comment> comments;

    @OneToMany(mappedBy = "caseFile")
    private List<Document> documents;
}
