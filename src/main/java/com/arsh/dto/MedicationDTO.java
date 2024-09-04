package com.arsh.dto;

import com.arsh.enums.DrugClass;
import com.arsh.model.Doctor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
// @BrandOrGeneric - should i add this here?
public class MedicationDTO {
    private int medicationInfoId;
    private String brandName;
    private String genericName;

    @NotNull(message = "Drug class cannot be null")
    private DrugClass drugClass;

    @NotNull(message = "Sub-category cannot be null")
    @Size(max = 50, message = "Sub-category must be less than or equal to 50 characters")
    private String subCategory;

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
    private Doctor prescribingDoctor;

    @Size(max = 100, message = "Pharmacy must be less than or equal to 100 characters")
    private String pharmacy;

    @Size(max = 500, message = "Comments must be less than or equal to 500 characters")
    private String comments;
}
