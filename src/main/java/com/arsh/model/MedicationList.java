package com.arsh.model;

import com.arsh.dto.MedicationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationList {
    private int medicationListId;
    private UUID patientId;
    private List<MedicationDTO> medicationList;
    private LocalDateTime lastChanged;
}
