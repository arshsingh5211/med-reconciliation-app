package com.arsh.service;


import com.arsh.dao.MedicationDao;
import com.arsh.model.Medication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
