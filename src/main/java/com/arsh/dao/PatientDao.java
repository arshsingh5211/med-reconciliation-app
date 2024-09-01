package com.arsh.dao;

import com.arsh.model.Patient;

import java.util.List;
import java.util.UUID;

public interface PatientDao {
    Patient getPatient(UUID patientId);
    List<Patient> getAllPatients();
    void savePatient(Patient patient);
    void deletePatient(UUID patientId);
}
