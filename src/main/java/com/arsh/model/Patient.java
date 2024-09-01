package com.arsh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    private int patientId;
    private String firstName;
    private String lastName;
    private Date dob;
    private String phoneNumber;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String primaryDoctor;
    private String diseases;
    private String emergencyContactName;
    private String emergencyContactPhone;

}
