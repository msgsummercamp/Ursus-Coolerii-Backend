package com.example.airassist.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String content;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "case_id")
    private CaseFile caseFile;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime sentTime;
}
