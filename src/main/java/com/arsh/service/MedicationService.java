package com.arsh.service;

import com.arsh.dao.MedicationDao;
import com.arsh.dto.MedicationDTO;
import com.arsh.exception.MedicationValidationException;
import com.arsh.model.Medication;
import com.arsh.model.MedicationInfo;
import com.arsh.model.MedicationList;
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

    public MedicationList getMedicationListByPatientId(UUID patientId) {
        return medicationDao.getMedicationListByPatientId(patientId);
    }

    public void saveMedicationToMedicationList(UUID patientId, MedicationDTO medicationDTO) {
        validateMedicationDTO(medicationDTO);
        medicationDao.saveMedicationToMedList(patientId, medicationDTO);
    }

    // Update an existing medication in the patient's list
    public void updateMedicationInfo(int medicationInfoId, MedicationDTO medicationDTO) {
        validateMedicationDTO(medicationDTO);
        medicationDao.updateMedicationOnMedList(medicationDTO);
    }

    public void deleteMedicationInfo(int medicationInfoId) {
        medicationDao.deleteMedicationFromMedList(medicationInfoId);
    }

    public MedicationInfo getMedicationInfo(int medicationInfoId) {
        return medicationDao.getMedicationFromMedListById(medicationInfoId);
    }

    private void validateMedicationDTO(MedicationDTO medicationDTO) {
        if (medicationDTO.getBrandName() == null && medicationDTO.getGenericName() == null) {
            throw new MedicationValidationException("Either brand name or generic name must be provided.");
        }
        if (medicationDTO.getDosage() == null || medicationDTO.getDosage().isEmpty()) {
            throw new MedicationValidationException("Dosage cannot be null or empty.");
        }
        if (medicationDTO.getFrequency() == null || medicationDTO.getFrequency().isEmpty()) {
            throw new MedicationValidationException("Frequency cannot be null or empty.");
        }
        // Add more validation as needed
    }
}
