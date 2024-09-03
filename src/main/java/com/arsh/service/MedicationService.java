package com.arsh.service;

import com.arsh.dao.MedicationDao;
import com.arsh.model.Medication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class MedicationService {
    private final MedicationDao medicationDao;

    @Autowired
    public MedicationService (MedicationDao medicationDao) {
        this.medicationDao = medicationDao;
    }

    public Medication getMedication(int medicationId) {
        return medicationDao.getMedication(medicationId);
    }

    public List<Medication> getAllMedications() {
        return medicationDao.getAllMedications();
    }

    public void saveMedication(Medication medication) {
        medicationDao.saveMedication(medication);
    }

    public void deleteMedication(int medicationId) {
        medicationDao.deleteMedication(medicationId);
    }

    public List<Medication> getMedicationListByPatientId(UUID patientId) {
        List<Medication> medications = medicationDao.getMedicationListByPatientId(patientId);
        if (medications.isEmpty()) {
            throw new NoSuchElementException("No medications found for patient ID: " + patientId);
        }
        return medications;
    }

    public void addMedicationToPatientList(UUID patientId, Medication medication) {
        try {
            medicationDao.addMedicationToPatientList(medication, patientId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save medication for patient ID: " + patientId, e);
        }
    }

    public void deleteMedicationFromPatientList(Medication med, UUID patientId) {
        try {
            medicationDao.deleteMedicationFromPatientList(med, patientId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete " + med.getName() + " for patient ID: " + patientId, e);
        }
    }
}
