package com.arsh.service;

import com.arsh.dao.MedicationDao;
import com.arsh.model.MedicationInfo;
import com.arsh.model.Medication;
import com.arsh.exception.MedicationNotFoundException;
import com.arsh.exception.MedicationValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MedicationService {

    private final MedicationDao medicationDao;

    @Autowired
    public MedicationService(MedicationDao medicationDao) {
        this.medicationDao = medicationDao;
    }

    // Fetch the list of medications for a specific patient
    public List<MedicationInfo> getMedicationListByPatientId(UUID patientId) {
        return medicationDao.getMedicationListByPatientId(patientId);
    }

    // Add a new medication to the patient's list with validation
    @Transactional(propagation = Propagation.REQUIRED)
    public void addMedication(MedicationInfo medicationInfo) {
        validateMedicationInfo(medicationInfo);
        medicationDao.saveMedicationInfo(medicationInfo);
    }

    // Update an existing medication in the patient's list with validation
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateMedicationInfo(int medicationInfoId, MedicationInfo updatedInfo) {
        MedicationInfo existingInfo = medicationDao.getMedicationInfoById(medicationInfoId);
        if (existingInfo == null) {
            throw new MedicationNotFoundException("MedicationInfo with ID " + medicationInfoId + " not found.");
        }
        validateMedicationInfo(updatedInfo);
        medicationDao.updateMedicationInfo(updatedInfo);
    }

    // Delete a medication from the patient's list
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteMedicationInfo(int medicationInfoId) {
        MedicationInfo existingInfo = medicationDao.getMedicationInfoById(medicationInfoId);
        if (existingInfo == null) {
            throw new MedicationNotFoundException("MedicationInfo with ID " + medicationInfoId + " not found.");
        }
        medicationDao.deleteMedicationInfo(medicationInfoId);
    }

    // Validate medication data
    private void validateMedicationInfo(MedicationInfo medicationInfo) {
        if (medicationInfo.getMedication() == null) {
            throw new MedicationValidationException("Medication cannot be null.");
        }
        if (medicationInfo.getDosage() == null || medicationInfo.getDosage().isEmpty()) {
            throw new MedicationValidationException("Dosage cannot be null or empty.");
        }
        // Add more validation as needed
    }
}
