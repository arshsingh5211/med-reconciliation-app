package com.arsh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientMedication {
    private int patientMedicationId;
    private UUID patientId;
    private int medicationId;
    private String dosage;
    private String frequency;
    private Date startDate;
    private Date endDate;
}
