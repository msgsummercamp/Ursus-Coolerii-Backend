package com.example.airassist.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "roles")
@AllArgsConstructor
@EqualsAndHashCode(exclude = "user")
public class Role {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id Long id;
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<User> user;

}
