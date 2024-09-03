package com.arsh.dao;

import com.arsh.model.Doctor;
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
    private final DoctorDao doctorDao;

    @Autowired
    public JdbcMedicationDao(JdbcTemplate jdbcTemplate, DoctorDao doctorDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.doctorDao = doctorDao;
    }

    @Override
    public List<Medication> getMedicationListByPatientId(UUID patientId) {
        String sql = "SELECT m.medication_id, m.name, m.dosage, m.frequency, m.route, " +
                "m.is_prn, m.date_started, m.is_current, m.pharmacy, m.comments, " +
                "d.doctor_id, d.first_name, d.last_name, d.specialty, d.phone_number, d.street_address, d.city, d.state, d.zip_code " +
                "FROM Medication m " +
                "LEFT JOIN Doctor d ON m.prescribing_doctor = d.doctor_id " +
                "WHERE m.patient_id = ?";

        return jdbcTemplate.query(sql, new Object[]{patientId}, new MedicationRowMapper());
    }

    @Override
    public void addMedicationToPatientList(Medication medication, UUID patientId) {
        String sql = "INSERT INTO Medication (patient_id, name, dosage, frequency, route, " +
                "is_prn, date_started, is_current, prescribing_doctor, pharmacy, comments) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                patientId,
                medication.getName(),
                medication.getDosage(),
                medication.getFrequency(),
                medication.getRoute(),
                medication.isPrn(),
                medication.getDateStarted(),
                medication.isCurrent(),
                medication.getPrescribingDoctor().getDoctorId(),
                medication.getPharmacy(),
                medication.getComments()
        );
    }

    @Override
    public void deleteMedicationFromPatientList(Medication medication, UUID patientId) {
        String sql = "DELETE FROM Medication WHERE medication_id = ? AND patient_id = ?";

        jdbcTemplate.update(sql, medication.getMedicationId(), patientId);
    }

    // RowMapper for mapping the result set to Medication
    private class MedicationRowMapper implements RowMapper<Medication> {
        @Override
        public Medication mapRow(ResultSet rs, int rowNum) throws SQLException {
            Medication medication = new Medication();
            medication.setMedicationId(rs.getInt("medication_id"));
            medication.setName(rs.getString("name"));
            medication.setDosage(rs.getString("dosage"));
            medication.setFrequency(rs.getString("frequency"));
            medication.setRoute(rs.getString("route"));
            medication.setPrn(rs.getBoolean("is_prn"));
            medication.setDateStarted(rs.getDate("date_started").toLocalDate());
            medication.setCurrent(rs.getBoolean("is_current"));
            medication.setPharmacy(rs.getString("pharmacy"));
            medication.setComments(rs.getString("comments"));

            // Map the Doctor fields
            Doctor doctor = new Doctor();
            doctor.setDoctorId((UUID) rs.getObject("doctor_id"));
            doctor.setFirstName(rs.getString("first_name"));
            doctor.setLastName(rs.getString("last_name"));
            doctor.setSpecialty(rs.getString("specialty"));
            doctor.setPhoneNumber(rs.getString("phone_number"));
            doctor.setStreetAddress(rs.getString("street_address"));
            doctor.setCity(rs.getString("city"));
            doctor.setState(rs.getString("state"));
            doctor.setZipCode(rs.getString("zip_code"));

            medication.setPrescribingDoctor(doctor);

            return medication;
        }
    }

}
