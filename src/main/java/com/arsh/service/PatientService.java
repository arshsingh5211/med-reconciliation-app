package com.arsh.service;

import com.arsh.dao.PatientDao;
import com.arsh.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PatientService {

    private final PatientDao patientDao;

    @Autowired
    public PatientService(PatientDao patientDao) {
        this.patientDao = patientDao;
    }

    public Patient getPatient(UUID patientId) {
        return patientDao.getPatient(patientId);
    }

    public List<Patient> getAllPatients() {
        return patientDao.getAllPatients();
    }

    public void savePatient(Patient patient) {
        patientDao.savePatient(patient);
    }

    public void deletePatient(UUID patientId) {
        patientDao.deletePatient(patientId);
    }
}