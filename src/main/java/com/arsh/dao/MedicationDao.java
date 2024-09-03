package com.arsh.dao;

import com.arsh.model.Medication;

import java.util.List;
import java.util.UUID;

public interface MedicationDao {
    Medication getMedication(int medicationId);

    List<Medication> getMedicationListByPatientId(UUID patientId);

    List<Medication> getAllMedications();

    void saveMedication(Medication medication);

    void addMedicationToPatientList(Medication medication, UUID patientId);

    void deleteMedicationFromPatientList(Medication medication, UUID patientId);

    void deleteMedication(int medicationId);
}
