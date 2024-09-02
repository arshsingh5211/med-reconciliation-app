package com.arsh.dao;

import com.arsh.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public JdbcPatientDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Patient getPatient(UUID patientId) {
        String sql = "SELECT patient_id, first_name, last_name, dob, primary_doctor, diseases, " +
                     "emergency_contact_name, emergency_contact_phone " +
                     "FROM Patient WHERE patient_id = ?";
        return jdbcTemplate.queryForObject(sql, new PatientRowMapper(), patientId);
    }

    @Override
    public List<Patient> getAllPatients() {
        String sql = "SELECT p.patient_id, p.first_name, p.last_name, pi.dob, pi.phone_number, " +
                "pi.street_address, pi.city, pi.state, pi.zip_code, " +
                "pi.emergency_contact_name, pi.emergency_contact_phone " +
                "FROM Patient p " +
                "LEFT JOIN PatientInfo pi ON p.patient_id = pi.patient_id";

        return jdbcTemplate.query(sql, new PatientRowMapper());
    }


    @Override
    public void savePatient(Patient patient) {
        String sql = "INSERT INTO Patient (first_name, last_name, dob, primary_doctor, diseases, " +
                     "emergency_contact_name, emergency_contact_phone) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, patient.getFirstName(), patient.getLastName(), patient.getDob(),
                            patient.getPrimaryDoctor(), patient.getDiseases(),
                            patient.getEmergencyContactName(), patient.getEmergencyContactPhone());
    }

    @Override
    public void deletePatient(UUID patientId) {
        String sql = "DELETE FROM Patient WHERE patient_id = ?";
        jdbcTemplate.update(sql, patientId);
    }

    // RowMapper implementation as a static inner class for mapping ResultSet to Patient objects
    public class PatientRowMapper implements RowMapper<Patient> {
        @Override
        public Patient mapRow(ResultSet rs, int rowNum) throws SQLException {
            Patient patient = new Patient();
            patient.setPatientId(rs.getObject("patient_id", UUID.class));
            patient.setFirstName(rs.getString("first_name"));
            patient.setLastName(rs.getString("last_name"));

            // Set fields from PatientInfo
            patient.setDob(rs.getDate("dob"));
            patient.setPhoneNumber(rs.getString("phone_number"));
            patient.setStreetAddress(rs.getString("street_address"));
            patient.setCity(rs.getString("city"));
            patient.setState(rs.getString("state"));
            patient.setZipCode(rs.getString("zip_code"));
            patient.setEmergencyContactName(rs.getString("emergency_contact_name"));
            patient.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));

            return patient;
        }
    }

}
