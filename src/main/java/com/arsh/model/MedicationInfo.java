package com.arsh.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationInfo {
    private int medicationInfoId;

    @NotNull(message = "MedicationId cannot be null")
    private Medication medicationId;

    @NotNull(message = "Patient ID cannot be null")
    private UUID patientId;

    @NotNull(message = "Dosage cannot be null")
    @Size(max = 50, message = "Dosage must be less than or equal to 50 characters")
    private String dosage;

    @NotNull(message = "Frequency cannot be null")
    @Size(max = 50, message = "Frequency must be less than or equal to 50 characters")
    private String frequency;

    @Size(max = 50, message = "Route must be less than or equal to 50 characters")
    private String route;   // Oral, IV, etc.

    private boolean isPrn;

    @NotNull(message = "Date started cannot be null")
    private LocalDate dateStarted;
    private boolean isCurrent;
    private UUID prescribingDoctorId;

    @Size(max = 100, message = "Pharmacy must be less than or equal to 100 characters")
    private String pharmacy;

    private LocalDateTime updatedAt;

    @Size(max = 500, message = "Comments must be less than or equal to 500 characters")
    private String comments;
}
