package com.arsh.dto;

import com.arsh.model.Disease;
import com.arsh.model.Doctor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {
    private UUID patientId;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String phoneNumber;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private Doctor primaryDoctor;
    private List<Disease> diseases;
}