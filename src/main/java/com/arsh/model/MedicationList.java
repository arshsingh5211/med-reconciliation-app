package com.arsh.model;

import com.arsh.dto.MedicationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationList {
    private int medicationListId;
    private List<MedicationDTO> medicationList;
}
