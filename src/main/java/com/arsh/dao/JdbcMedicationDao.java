package com.arsh.dao;

import com.arsh.enums.DrugClass;
import com.arsh.model.Doctor;
import com.arsh.model.Medication;
import com.arsh.model.MedicationInfo;
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

    // Methods for Medication (general medication data)

    @Override
    public Medication getMedicationById(int medicationId) {
        String sql = "SELECT * FROM Medication WHERE medication_id = ?";
        return jdbcTemplate.queryForObject(sql, new MedicationRowMapper(), medicationId);
    }

    @Override
    public List<Medication> getAllMedications() {
        String sql = "SELECT * FROM Medication";
        return jdbcTemplate.query(sql, new MedicationRowMapper());
    }

    @Override
    public void saveMedication(Medication medication) {
        String sql = "INSERT INTO Medication (brand_name, generic_name, drug_class, sub_category, is_generic) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                medication.getBrandName(),
                medication.getGenericName(),
                medication.getDrugClass().name(),
                medication.getSubCategory(),
                medication.isGeneric()
        );
    }

    // Methods for MedicationInfo (patient-specific medication data)

    @Override
    public MedicationInfo getMedicationInfoById(int medicationInfoId) {
        String sql = "SELECT * FROM MedicationInfo WHERE medication_info_id = ?";
        return jdbcTemplate.queryForObject(sql, new MedicationInfoRowMapper(), medicationInfoId);
    }

    @Override
    public List<MedicationInfo> getMedicationListByPatientId(UUID patientId) {
        String sql = "SELECT * FROM MedicationInfo WHERE patient_id = ?";
        return jdbcTemplate.query(sql, new MedicationInfoRowMapper(), patientId);
    }

    @Override
    public void saveMedicationInfo(MedicationInfo medicationInfo) {
        String sql = "INSERT INTO MedicationInfo (medication_id, patient_id, dosage, frequency, route, " +
                "is_prn, date_started, is_current, prescribing_doctor, pharmacy, comments) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                medicationInfo.getMedication().getMedicationId(),
                medicationInfo.getPatientId(),
                medicationInfo.getDosage(),
                medicationInfo.getFrequency(),
                medicationInfo.getRoute(),
                medicationInfo.isPrn(),
                medicationInfo.getDateStarted(),
                medicationInfo.isCurrent(),
                medicationInfo.getPrescribingDoctor().getDoctorId(),
                medicationInfo.getPharmacy(),
                medicationInfo.getComments()
        );
    }

    @Override
    public void updateMedicationInfo(MedicationInfo medicationInfo) {
        String sql = "UPDATE MedicationInfo SET dosage = ?, frequency = ?, route = ?, is_prn = ?, " +
                "date_started = ?, is_current = ?, prescribing_doctor = ?, pharmacy = ?, comments = ? " +
                "WHERE medication_info_id = ?";
        jdbcTemplate.update(sql,
                medicationInfo.getDosage(),
                medicationInfo.getFrequency(),
                medicationInfo.getRoute(),
                medicationInfo.isPrn(),
                medicationInfo.getDateStarted(),
                medicationInfo.isCurrent(),
                medicationInfo.getPrescribingDoctor().getDoctorId(),
                medicationInfo.getPharmacy(),
                medicationInfo.getComments(),
                medicationInfo.getMedicationInfoId()
        );
    }

    @Override
    public void deleteMedicationInfo(int medicationInfoId) {
        String sql = "DELETE FROM MedicationInfo WHERE medication_info_id = ?";
        jdbcTemplate.update(sql, medicationInfoId);
    }

    // RowMapper for Medication
    private static class MedicationRowMapper implements RowMapper<Medication> {
        @Override
        public Medication mapRow(ResultSet rs, int rowNum) throws SQLException {
            Medication medication = new Medication();
            medication.setMedicationId(rs.getInt("medication_id"));
            medication.setBrandName(rs.getString("brand_name"));
            medication.setGenericName(rs.getString("generic_name"));
            medication.setDrugClass(DrugClass.valueOf(rs.getString("drug_class")));
            medication.setSubCategory(rs.getString("sub_category"));
            medication.setGeneric(rs.getBoolean("is_generic"));
            return medication;
        }
    }

    // RowMapper for MedicationInfo
    private static class MedicationInfoRowMapper implements RowMapper<MedicationInfo> {
        @Override
        public MedicationInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            MedicationInfo medicationInfo = new MedicationInfo();
            medicationInfo.setMedicationInfoId(rs.getInt("medication_info_id"));

            // Create a Medication object and set its fields
            Medication medication = new Medication();
            medication.setMedicationId(rs.getInt("medication_id"));
            medication.setBrandName(rs.getString("brand_name"));
            medication.setGenericName(rs.getString("generic_name"));
            medication.setDrugClass(DrugClass.valueOf(rs.getString("drug_class")));
            medication.setSubCategory(rs.getString("sub_category"));
            medication.setGeneric(rs.getBoolean("is_generic"));

            // Set the Medication object in MedicationInfo
            medicationInfo.setMedication(medication);

            // Set the other fields in MedicationInfo
            medicationInfo.setPatientId((UUID) rs.getObject("patient_id"));
            medicationInfo.setDosage(rs.getString("dosage"));
            medicationInfo.setFrequency(rs.getString("frequency"));
            medicationInfo.setRoute(rs.getString("route"));
            medicationInfo.setPrn(rs.getBoolean("is_prn"));
            medicationInfo.setDateStarted(rs.getDate("date_started").toLocalDate());
            medicationInfo.setCurrent(rs.getBoolean("is_current"));
            medicationInfo.setPharmacy(rs.getString("pharmacy"));
            medicationInfo.setComments(rs.getString("comments"));

            // Assuming a separate query for Doctor to avoid nested joins here
            UUID doctorId = (UUID) rs.getObject("prescribing_doctor");
            if (doctorId != null) {
                Doctor doctor = new Doctor();
                doctor.setDoctorId(doctorId);
                medicationInfo.setPrescribingDoctor(doctor);
            }

            return medicationInfo;
        }
    }
}
