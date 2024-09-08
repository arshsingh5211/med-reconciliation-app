package com.arsh.dao;

import com.arsh.dto.PatientDTO;
import com.arsh.exception.PatientNotFoundException;
import com.arsh.model.Doctor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class JdbcPatientDao implements PatientDao {

    private final JdbcTemplate jdbcTemplate;
    private final DoctorDao doctorDao;
    private static final Logger logger = LoggerFactory.getLogger(JdbcPatientDao.class);

    @Autowired
    public JdbcPatientDao(JdbcTemplate jdbcTemplate, DoctorDao doctorDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.doctorDao = doctorDao;
    }

    @Override
    public PatientDTO getPatient(UUID patientId) {
        String sql = "SELECT p.patient_id, p.first_name, p.last_name, pi.dob, pi.phone_number, " +
                "pi.street_address, pi.city, pi.state, pi.zip_code, pi.primary_doctor, " +
                "pi.emergency_contact_name, pi.emergency_contact_phone " +
                "FROM Patient p LEFT JOIN PatientInfo pi ON p.patient_id = pi.patient_id WHERE p.patient_id = ?";
        try {
            PatientDTO patient = jdbcTemplate.queryForObject(sql, new PatientRowMapper(doctorDao), patientId);
            logger.info("Successfully fetched patient with ID: {}", patientId);
            return patient;
        } catch (EmptyResultDataAccessException e) {
            logger.error("Patient with ID {} not found. SQL: {}, Error: {}", patientId, sql, e.getMessage());
            throw new PatientNotFoundException("Patient not found with ID: " + patientId);
        } catch (Exception e) {
            logger.error("Error fetching patient with ID {}. SQL: {}, Error: {}", patientId, sql, e.getMessage());
            throw new RuntimeException("Failed to fetch patient", e);
        }
    }

    @Override
    public List<PatientDTO> getAllPatients() {
        String sql = "SELECT p.patient_id, " +
                "       p.first_name, " +
                "       p.last_name, " +
                "       pi.dob, " +
                "       pi.phone_number, " +
                "       pi.street_address, " +
                "       pi.city, " +
                "       pi.state, " +
                "       pi.zip_code, " +
                "       pi.primary_doctor, " +
                "       pi.emergency_contact_name, " +
                "       pi.emergency_contact_phone " +
                "FROM Patient p " +
                "LEFT JOIN PatientInfo pi ON p.patient_id = pi.patient_id";

        return jdbcTemplate.query(sql, new PatientRowMapper(doctorDao));
    }

    @Override
    public PatientDTO savePatient(PatientDTO patientDTO) {
        try {
            String patientSql = "INSERT INTO Patient (first_name, last_name) VALUES (?, ?) RETURNING patient_id";
            UUID patientId = jdbcTemplate.queryForObject(patientSql, UUID.class, patientDTO.getFirstName(),
                    patientDTO.getLastName());

            String patientInfoSql = "INSERT INTO PatientInfo (patient_id, dob, phone_number, street_address, city, state, zip_code, primary_doctor, emergency_contact_name, emergency_contact_phone) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(patientInfoSql, patientId,
                    patientDTO.getDob(),
                    patientDTO.getPhoneNumber(),
                    patientDTO.getStreetAddress(),
                    patientDTO.getCity(),
                    patientDTO.getState(),
                    patientDTO.getZipCode(),
                    patientDTO.getPrimaryDoctor() != null ? patientDTO.getPrimaryDoctor().getDoctorId() : null,
                    patientDTO.getEmergencyContactName(),
                    patientDTO.getEmergencyContactPhone()
            );
            patientDTO.setPatientId(patientId);
            return patientDTO;
        } catch (Exception e) {
            logger.error("Error saving patient: " + e.getMessage());
            throw new RuntimeException("Failed to save patient", e);
        }
    }

    @Override
    public void deletePatient(UUID patientId) {
        try {
            deleteMedicationsByPatientId(patientId);
            deletePatientInfo(patientId);
            String sql = "DELETE FROM Patient WHERE patient_id = ?";
            jdbcTemplate.update(sql, patientId);
        } catch (Exception e) {
            System.err.println("Error deleting patient: " + e.getMessage());
            throw new RuntimeException("Failed to delete patient", e);
        }
    }

    private void deleteMedicationsByPatientId(UUID patientId) {
        String sqlMedicationInfo = "DELETE FROM MedicationInfo WHERE medication_list_id IN (SELECT medication_list_id FROM MedicationList WHERE patient_id = ?)";
        jdbcTemplate.update(sqlMedicationInfo, patientId);

        String sqlMedicationList = "DELETE FROM MedicationList WHERE patient_id = ?";
        jdbcTemplate.update(sqlMedicationList, patientId);
    }

    private void deletePatientInfo(UUID patientId) {
        String sql = "DELETE FROM PatientInfo WHERE patient_id = ?";
        jdbcTemplate.update(sql, patientId);
    }


    // RowMapper implementation as a static inner class for mapping ResultSet to Patient objects
    private static class PatientRowMapper implements RowMapper<PatientDTO> {
        private final DoctorDao doctorDao;

        public PatientRowMapper(DoctorDao doctorDao) {
            this.doctorDao = doctorDao;
        }

        @Override
        public PatientDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            PatientDTO dto = new PatientDTO();
            dto.setPatientId((UUID) rs.getObject("patient_id"));
            dto.setFirstName(rs.getString("first_name"));
            dto.setLastName(rs.getString("last_name"));
            dto.setDob(rs.getDate("dob").toLocalDate());
            dto.setPhoneNumber(rs.getString("phone_number"));
            dto.setStreetAddress(rs.getString("street_address"));
            dto.setCity(rs.getString("city"));
            dto.setState(rs.getString("state"));
            dto.setZipCode(rs.getString("zip_code"));
            dto.setEmergencyContactName(rs.getString("emergency_contact_name"));
            dto.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));

            UUID primaryDoctorId = (UUID) rs.getObject("primary_doctor");
            Doctor primaryDoctor = primaryDoctorId != null ? doctorDao.getDoctorById(primaryDoctorId) : null;
            dto.setPrimaryDoctor(primaryDoctor);

            return dto;
        }
    }
}
