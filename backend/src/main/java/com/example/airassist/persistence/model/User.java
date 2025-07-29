package com.example.airassist.persistence.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id Long id;

    @NotBlank(message = "User should have an email")
    @Email(message = "This field should be a valid email")
    private String email;

    @NotBlank(message = "User should have a password")
    private String password;

    @NotBlank(message = "User should have a first name")
    private String firstName;

    @NotBlank(message = "User should have a last name")
    private String lastName;

    @NotBlank(message = "User should enter date of birth")
    private Date dateOfBirth;

    @NotBlank(message = "User should have a phone number")
    private String phoneNumber;

    @NotBlank(message = "User should have an address")
    private String address;

    @NotBlank(message = "User should have a postal code")
    private String postalCode;

    private boolean isFirstLogin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> role;
}
