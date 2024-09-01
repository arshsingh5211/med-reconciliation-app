package com.arsh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientInfo {

    private int infoId;
    private UUID patientId;
    private Date dob;
    private String phoneNumber;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private Doctor primaryDoctor;
    private String emergencyContactName;
    private String emergencyContactPhone;
}
