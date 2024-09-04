package com.arsh.model;

import com.arsh.dto.MedicationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationList {
    private UUID patientId;
    private List<MedicationDTO> medicationList;
    private LocalDate lastChanged;
}
