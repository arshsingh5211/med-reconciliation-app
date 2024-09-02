package com.arsh.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class PatientDTO {

    private Long patientId;
    private String firstName;
    private String lastName;
    private Date dob;
    private String phoneNumber;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String primaryDoctor;

    // Constructor
    public PatientDTO(Long patientId, String firstName, String lastName, Date dob,
                      String phoneNumber, String streetAddress, String city, String state,
                      String zipCode, String emergencyContactName, String emergencyContactPhone,
                      String primaryDoctor) {
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.phoneNumber = phoneNumber;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.primaryDoctor = primaryDoctor;
    }
}
