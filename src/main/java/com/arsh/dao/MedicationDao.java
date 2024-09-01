package com.arsh.dao;

import com.arsh.model.Medication;

import java.util.List;

public interface MedicationDao {
    Medication getMedication(int medicationId);

    List<Medication> getAllMedications();

    void saveMedication(Medication medication);

    void deleteMedication(int medicationId);
}
