package com.example.airassist.persistence.model;

import com.example.airassist.common.enums.CaseStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.ToString;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "cases")
@ToString(exclude = {"caseFlights", "comments", "documents"})
public class CaseFile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID caseId;

    private String contractId;

    private Timestamp caseDate;

    @NotBlank(message = "The case should contain the reservation number")
    private String reservationNumber;

    private CaseStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    private DisruptionDetails disruptionDetails;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id",
            foreignKey = @ForeignKey(name = "fk_case_file_user",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL"))
    private User user;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name= "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne
    @JoinColumn(name = "employee_id",
        foreignKey  = @ForeignKey(name = "fk_case_file_employee",
            foreignKeyDefinition = "FOREIGN KEY (employee_id) REFERENCES users(id) ON DELETE SET NULL"))
    private User employee;

    @JsonManagedReference
    @OneToMany(mappedBy = "caseFile", cascade = CascadeType.REMOVE)
    private List<CaseFlights> caseFlights;

    @OneToMany(mappedBy = "caseFile")
    @JsonManagedReference
    private List<Comment> comments;

    @OneToMany(mappedBy = "caseFile", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JsonManagedReference
    private List<Document> documents;
}
