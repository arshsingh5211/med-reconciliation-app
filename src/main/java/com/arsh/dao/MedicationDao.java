package com.arsh.dao;

import com.arsh.model.Medication;
import com.arsh.model.MedicationInfo;

import java.util.List;
import java.util.UUID;

public interface MedicationDao {
    // Methods related to Medication (general medication data for testing)
    Medication getMedicationById(int medicationId);
    void saveMedication(Medication medication);
    void deleteMedication(int medicationId);
    List<Medication> getAllMedications();

    // Methods related to medication lists (patient-specific medication data)
    List<MedicationInfo> getMedicationListByPatientId(UUID patientId);
    void saveMedicationToMedList(MedicationInfo medicationInfo);
    void updateMedicationOnMedList(MedicationInfo medicationInfo);
    void deleteMedicationFromMedList(int medicationInfoId);
    MedicationInfo getMedicationFromMedListById(int medicationInfoId);
}
