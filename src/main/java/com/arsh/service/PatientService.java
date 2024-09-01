package com.arsh.service;

import com.arsh.dao.PatientDao;
import com.arsh.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientDao patientDao;

    @Autowired
    public PatientService(PatientDao patientDao) {
        this.patientDao = patientDao;
    }

    public Patient getPatient(int patientId) {
        return patientDao.getPatient(patientId);
    }

    public List<Patient> getAllPatients() {
        return patientDao.getAllPatients();
    }

    public void savePatient(Patient patient) {
        patientDao.savePatient(patient);
    }

    public void deletePatient(int patientId) {
        patientDao.deletePatient(patientId);
    }
}