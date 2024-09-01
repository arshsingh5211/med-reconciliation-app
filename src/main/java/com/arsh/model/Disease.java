package com.arsh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Disease {

    private int diseaseId;
    private String name;
    private String severity;  // Mild, Moderate, Severe, etc.
}

// TODO: i want to consume from some api so there is a link to some official documentation about whatever disease