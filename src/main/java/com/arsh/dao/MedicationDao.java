package com.arsh.dao;

import com.arsh.model.Medication;

import java.util.List;
import java.util.UUID;

public interface MedicationDao {
    List<Medication> getMedicationListByPatientId(UUID patientId);

    void addMedicationToPatientList(Medication medication, UUID patientId);

    void deleteMedicationFromPatientList(Medication medication, UUID patientId);

}
