package com.arsh.dao;

import com.arsh.model.Medication;
import com.arsh.model.MedicationInfo;

import java.util.List;
import java.util.UUID;

public interface MedicationDao {
    // Methods related to Medication (general medication data)
    Medication getMedicationById(int medicationId);
    List<Medication> getAllMedications();
    void saveMedication(Medication medication);

    // Methods related to medication lists (patient-specific medication data)
    MedicationInfo getMedicationInfoById(int medicationInfoId);
    List<MedicationInfo> getMedicationListByPatientId(UUID patientId);
    void saveMedicationInfo(MedicationInfo medicationInfo);
    void updateMedicationInfo(MedicationInfo medicationInfo);
    void deleteMedicationInfo(int medicationInfoId);
}
