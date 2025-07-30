package com.example.airassist.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import java.util.Date;


@Data
@Entity
public class Passenger {

    private @Id
    @GeneratedValue Long id;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String phoneNumber;
    private String address;
    private String postalCode;

}