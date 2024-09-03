package com.arsh.dao;

import com.arsh.dto.MedicationDTO;
import com.arsh.model.Medication;
import com.arsh.model.MedicationInfo;
import com.arsh.model.MedicationList;

import java.util.List;
import java.util.UUID;

public interface MedicationDao {
    // Methods related to Medication (general medication data for testing)
    Medication getMedicationById(int medicationId);
    void saveMedication(Medication medication);
    void deleteMedication(int medicationId);
    List<Medication> getAllMedications();

    // Methods related to medication lists (patient-specific medication data)
    MedicationList getMedicationListByPatientId(UUID patientId);
    void saveMedicationToMedList(UUID patientId, MedicationDTO medicationDTO);
    void updateMedicationOnMedList(MedicationDTO medicationDTO);
    void deleteMedicationFromMedList(int medicationInfoId);
    MedicationInfo getMedicationFromMedListById(int medicationInfoId);
}
