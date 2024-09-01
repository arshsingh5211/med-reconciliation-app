package com.arsh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medication {
    private int medicationId;
    private String name;
    private String dosage;
    private String frequency;
    private UUID patientId;
}
