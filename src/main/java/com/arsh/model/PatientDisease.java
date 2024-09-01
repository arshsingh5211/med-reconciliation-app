package com.arsh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDisease {
    private int patientDiseaseId;
    private UUID patientId;
    private int diseaseId;
}
