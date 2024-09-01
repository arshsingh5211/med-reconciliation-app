package com.arsh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interaction {
    private int interactionId;
    private int medicationAId;
    private int medicationBId;
    private String severity; // mild, moderate, severe
    private String description;
}
