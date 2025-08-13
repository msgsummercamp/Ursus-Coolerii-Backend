package com.example.airassist.common.dto;

import lombok.Data;
import java.util.Date;

@Data
public class PassengerDTO {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String email;
    private String phone;
    private String address;
    private String postalCode;
}