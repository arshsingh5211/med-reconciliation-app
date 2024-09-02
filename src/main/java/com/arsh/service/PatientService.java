package com.arsh.service;

import com.arsh.dao.PatientDao;
import com.arsh.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Map;
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

    public void updatePatient(UUID patientId, Map<String, Object> updates) {
        Patient existingPatient = patientDao.getPatient(patientId);
        if (existingPatient == null) {
            throw new IllegalArgumentException("Patient with ID " + patientId + " not found.");
        }

        updates.forEach((key, value) -> {
            switch (key) {
                case "firstName" -> existingPatient.setFirstName(validateString((String) value));
                case "lastName" -> existingPatient.setLastName(validateString((String) value));
                case "dob" -> existingPatient.setDob(validateDate((String) value));
                case "phoneNumber" -> existingPatient.setPhoneNumber(validatePhoneNumber((String) value));
                case "streetAddress" -> existingPatient.setStreetAddress(validateString((String) value));
                case "city" -> existingPatient.setCity(validateString((String) value));
                case "state" -> existingPatient.setState(validateString((String) value));
                case "zipCode" -> existingPatient.setZipCode(validateString((String) value));
                case "primaryDoctor" -> existingPatient.setPrimaryDoctor(validateString((String) value));
                case "diseases" -> existingPatient.setDiseases(validateString((String) value));
                case "emergencyContactName" -> existingPatient.setEmergencyContactName(validateString((String) value));
                case "emergencyContactPhone" -> existingPatient.setEmergencyContactPhone(validatePhoneNumber((String) value));
                default -> throw new IllegalArgumentException("Invalid field: " + key);
            }
        });

        patientDao.savePatient(existingPatient);
    }

    private String validateString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("String value cannot be null or empty.");
        }
        return value;
    }

    private Date validateDate(String value) {
        try {
            return Date.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format is YYYY-MM-DD.");
        }
    }

    private String validatePhoneNumber(String value) {
        if (!value.matches("\\d{10}|(?:\\d{3}-){2}\\d{4}")) {
            throw new IllegalArgumentException("Invalid phone number format. Expected format is 123-456-7890 or 1234567890.");
        }
        return value;
    }
}
