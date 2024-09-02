package com.arsh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    private UUID patientId;
    private String firstName;
    private String lastName;
    private PatientInfo patientInfo;  // This would be a new class representing the PatientInfo table
    private List<Doctor> doctors;  // List of doctors associated with the patient
    private List<Disease> diseases;  // List of diseases associated with the patient
}
