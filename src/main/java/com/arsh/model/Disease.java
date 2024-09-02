package com.arsh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Disease {

    private UUID diseaseId;
    private String name;
    private String severity;  // Mild, Moderate, Severe, etc.
}
