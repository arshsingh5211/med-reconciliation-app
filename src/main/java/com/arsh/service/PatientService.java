package com.arsh.service;

import com.arsh.dao.PatientDao;
import com.arsh.dto.PatientDTO;
import com.arsh.exception.PatientNotFoundException;
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

    public PatientDTO getPatient(UUID patientId) {
        PatientDTO patient = patientDao.getPatient(patientId);
        if (patient == null) {
            throw new PatientNotFoundException("Patient with ID " + patientId + " not found.");
        }
        return patient;
    }

    public List<PatientDTO> getAllPatients() {
        return patientDao.getAllPatients();
    }

    public void savePatient(PatientDTO patientDTO) {
        patientDao.savePatient(patientDTO);
    }

    public void deletePatient(UUID patientId) {
        PatientDTO patient = getPatient(patientId);
        if (patient != null) {
            patientDao.deletePatient(patientId);
        } else {
            throw new PatientNotFoundException("Cannot delete, Patient with ID " + patientId + " not found.");
        }
    }
}
