package com.arsh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDoctor {
    private UUID patientDoctorId;
    private UUID patientId;
    private UUID doctorId;
    private String specialty; // Primary Care, Specialist, etc.
}
