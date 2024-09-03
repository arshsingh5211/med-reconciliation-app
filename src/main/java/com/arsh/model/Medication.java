package com.arsh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medication {
    private int medicationId;

    @NotNull(message = "Name cannot be null")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Dosage cannot be null")
    private String dosage;

    @NotNull(message = "Frequency cannot be null")
    private String frequency;

    private String route;
    private boolean isPrn;

    @NotNull(message = "Date started cannot be null")
    private LocalDate dateStarted;

    private boolean isCurrent;

    @NotNull(message = "Prescribing doctor cannot be null")
    private Doctor prescribingDoctor;

    private String pharmacy;
    private String comments;
}
