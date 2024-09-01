package com.arsh.dao;

import com.arsh.model.Patient;

import java.util.List;

public interface PatientDao {
    Patient getPatient(int patientId);
    List<Patient> getAllPatients();
    void savePatient(Patient patient);
    void deletePatient(int patientId);
}
