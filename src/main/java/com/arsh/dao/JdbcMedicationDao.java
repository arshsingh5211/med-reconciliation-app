package com.arsh.dao;

import com.arsh.model.Medication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class JdbcMedicationDao implements MedicationDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcMedicationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Medication getMedication(int medicationId) {
        String sql = "SELECT medication_id, name, dosage, frequency, patientId, " +
                "FROM Patient WHERE medication_id = ?";
        return jdbcTemplate.queryForObject(sql, new MedicationRowMapper(), medicationId);
    }

    @Override
    public List<Medication> getAllMedications() {
        String sql = "SELECT medication_id, name, dosage, frequency, patientId, " +
                "FROM Patient";
        return jdbcTemplate.query(sql, new MedicationRowMapper());
    }

    @Override
    public void saveMedication(Medication medication) {
        String sql = "INSERT INTO Medication (name, dosage, frequency, patientId) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, medication.getName(), medication.getDosage(), medication.getFrequency(),
                medication.getPatientId());
    }

    @Override
    public void deleteMedication(int medicationId) {
        String sql = "DELETE FROM Medication WHERE medication_id = ?";
        jdbcTemplate.update(sql, medicationId);
    }

    // RowMapper implementation as a static inner class for mapping ResultSet to Medication objects
    private static final class MedicationRowMapper implements RowMapper<Medication> {
        @Override
        public Medication mapRow(ResultSet rs, int rowNum) throws SQLException {
            Medication medication = new Medication();
            medication.setMedicationId(rs.getInt("medication_id"));
            medication.setName(rs.getString("name"));
            medication.setDosage(rs.getString("dosage"));
            medication.setFrequency(rs.getString("frequency"));
            medication.setPatientId(UUID.fromString(rs.getString("patient_id")));
            return medication;
        }
    }
}
