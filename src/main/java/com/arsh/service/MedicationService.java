package com.arsh.service;

import com.arsh.dao.MedicationDao;
import com.arsh.exception.MedicationValidationException;
import com.arsh.model.Medication;
import com.arsh.model.MedicationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public Medication getMedication(int medicationId) {
        return medicationDao.getMedicationById(medicationId);
    }

    public void saveMedication (Medication medication) {
        medicationDao.saveMedication(medication);
    }

    public void deleteMedication (int medicationId) {
        medicationDao.deleteMedication(medicationId);
    }

    public List<Medication> getAllMedications() {
        return medicationDao.getAllMedications();
    }

    public List<MedicationInfo> getMedicationListByPatientId(UUID patientId) {
        return medicationDao.getMedicationListByPatientId(patientId);
    }

    // Update an existing medication in the patient's list with validation
//    public void updateMedicationInfo(int medicationInfoId, MedicationInfo updatedInfo) {
//        MedicationInfo existingInfo = medicationDao.getMedicationInfoById(medicationInfoId);
//        if (existingInfo == null) {
//            throw new MedicationNotFoundException("MedicationInfo with ID " + medicationInfoId + " not found.");
//        }
//        validateMedicationInfo(updatedInfo);
//        medicationDao.updateMedicationInfo(updatedInfo);
//    }

//    public void deleteMedicationInfo(int medicationInfoId) {
//        MedicationInfo existingInfo = medicationDao.getMedicationInfoById(medicationInfoId);
//        if (existingInfo == null) {
//            throw new MedicationNotFoundException("MedicationInfo with ID " + medicationInfoId + " not found.");
//        }
//        medicationDao.deleteMedicationInfo(medicationInfoId);
//    }

    private void validateMedicationInfo(MedicationInfo medicationInfo) {
        if (medicationInfo.getMedicationId() == null) {
            throw new MedicationValidationException("Medication cannot be null.");
        }
        if (medicationInfo.getDosage() == null || medicationInfo.getDosage().isEmpty()) {
            throw new MedicationValidationException("Dosage cannot be null or empty.");
        }
        // Add more validation as needed
    }
}
