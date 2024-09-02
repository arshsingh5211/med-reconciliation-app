package com.arsh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    private UUID doctorId;
    private String firstName;
    private String lastName;
    private String specialty;
    private String phoneNumber;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
}
