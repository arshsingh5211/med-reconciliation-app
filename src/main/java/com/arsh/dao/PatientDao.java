package com.arsh.dao;

import com.arsh.dto.PatientDTO;
import java.util.List;
import java.util.UUID;

public interface PatientDao {
    PatientDTO getPatient(UUID patientId);
    List<PatientDTO> getAllPatients();
    PatientDTO savePatient(PatientDTO patientDTO);
    void deletePatient(UUID patientId);
}
