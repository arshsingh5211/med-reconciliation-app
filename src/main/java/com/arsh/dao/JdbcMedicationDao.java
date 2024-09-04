package com.arsh.dao;

import com.arsh.dto.MedicationDTO;
import com.arsh.enums.DrugClass;
import com.arsh.model.Doctor;
import com.arsh.model.Medication;
import com.arsh.model.MedicationInfo;
import com.arsh.model.MedicationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class JdbcMedicationDao implements MedicationDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcMedicationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------
    // Methods for Medication (general medication data mostly for testing)
    @Override
    public Medication getMedicationById(int medicationId) {
        String sql = "SELECT * FROM Medication WHERE medication_id = ?";
        return jdbcTemplate.queryForObject(sql, new MedicationRowMapper(), medicationId);
    }

    @Override
    public void saveMedication(Medication medication) {
        String sql = "INSERT INTO Medication (brand_name, generic_name, drug_class, sub_category) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, medication.getBrandName(), medication.getGenericName(),
                medication.getDrugClass(), medication.getSubCategory());
    }

    @Override
    public void deleteMedication(int medicationId) {
        String sql = "DELETE FROM Medication WHERE medication_id = ?";
        jdbcTemplate.update(sql, medicationId);
    }

    @Override
    public List<Medication> getAllMedications() {
        String sql = "SELECT * FROM Medication";
        return jdbcTemplate.query(sql, new MedicationRowMapper());
    }




    // -------------------------------------------------------------------
    // Functional Methods for Manipulating Patient's Medication Lists
    @Override
    public MedicationList getMedicationListByPatientId(UUID patientId) {
        String sql = "SELECT * FROM medication_list WHERE patient_id = ?";
        return jdbcTemplate.queryForObject(sql, new MedicationListRowMapper(), patientId);
    }


    @Override
    public void saveMedicationToMedList(UUID patientId, MedicationDTO medicationDTO) {
    // First, check if the medication already exists
    String checkMedSql = "SELECT medication_id FROM Medication WHERE brand_name = ? OR generic_name = ?";
    Integer medicationId = jdbcTemplate.queryForObject(checkMedSql, Integer.class,
            medicationDTO.getBrandName(), medicationDTO.getGenericName());

    // If the medication does not exist, insert it
    if (medicationId == null) {
        String insertMedSql = "INSERT INTO Medication (brand_name, generic_name, drug_class, sub_category, is_generic) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING medication_id";
        medicationId = jdbcTemplate.queryForObject(insertMedSql, Integer.class,
                medicationDTO.getBrandName(), medicationDTO.getGenericName(),
                medicationDTO.getDrugClass(), medicationDTO.getSubCategory());
    }

    // Insert the patient-specific medication information into MedicationInfo
    String insertMedInfoSql = "INSERT INTO MedicationInfo (medication_id, patient_id, dosage, frequency, route, " +
            "is_prn, date_started, is_current, prescribing_doctor, pharmacy, comments) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(insertMedInfoSql,
            medicationId, patientId,
            medicationDTO.getDosage(), medicationDTO.getFrequency(), medicationDTO.getRoute(),
            medicationDTO.isPrn(), medicationDTO.getDateStarted(), medicationDTO.isCurrent(),
            medicationDTO.getPrescribingDoctor(), medicationDTO.getPharmacy(), medicationDTO.getComments());
    }

    @Override
    public void updateMedicationOnMedList(MedicationDTO medicationDTO) {
        String sql = "UPDATE MedicationInfo SET dosage = ?, frequency = ?, route = ?, is_prn = ?, " +
                "date_started = ?, is_current = ?, prescribing_doctor = ?, pharmacy = ?, comments = ? " +
                "WHERE medication_info_id = ?";
        jdbcTemplate.update(sql,
                medicationDTO.getDosage(),
                medicationDTO.getFrequency(),
                medicationDTO.getRoute(),
                medicationDTO.isPrn(),
                medicationDTO.getDateStarted(),
                medicationDTO.isCurrent(),
                medicationDTO.getPrescribingDoctor() != null ? medicationDTO.getPrescribingDoctor().getDoctorId() : null,
                medicationDTO.getPharmacy(),
                medicationDTO.getComments(),
                medicationDTO.getMedicationInfoId()  // Assuming this is the medication_info_id
        );
    }


    @Override
    public void deleteMedicationFromMedList(int medicationInfoId) {
        String sql = "DELETE FROM MedicationInfo WHERE medication_info_id = ?";
        jdbcTemplate.update(sql, medicationInfoId);
    }

    @Override
    public MedicationInfo getMedicationFromMedListById(int medicationInfoId) {
        String sql = "SELECT mi.*, m.*, d.* FROM MedicationInfo mi " +
                "JOIN Medication m ON mi.medication_id = m.medication_id " +
                "LEFT JOIN Doctor d ON mi.prescribing_doctor = d.doctor_id " +
                "WHERE mi.medication_info_id = ?";
        return jdbcTemplate.queryForObject(sql, new MedicationInfoRowMapper(), medicationInfoId);
    }






    // -------------------------------------------------------------------
    // HELPER METHODS ONLY
    private Integer getMedicationIdByName(String brandName, String genericName) {
        String sql = "SELECT medication_id FROM Medication WHERE brand_name = ? OR generic_name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, brandName, genericName);
    }

    private Integer getMedicationInfoId(MedicationInfo medicationInfo) {
        String sql = "SELECT medication_info_id FROM MedicationInfo WHERE medication_id = ? AND patient_id = ? " +
                     "AND dosage = ? AND frequency = ? AND route = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, medicationInfo.getMedicationId(),
                    medicationInfo.getPatientId(), medicationInfo.getDosage(), medicationInfo.getFrequency(), medicationInfo.getRoute());
        } catch (Exception e) {
            return null;
        }
    }






    // RowMapper for Medication
    private static class MedicationRowMapper implements RowMapper<Medication> {
        @Override
        public Medication mapRow(ResultSet rs, int rowNum) throws SQLException {
            Medication medication = new Medication();
            medication.setMedicationId(rs.getInt("medication_id"));
            medication.setBrandName(rs.getString("brand_name"));
            medication.setGenericName(rs.getString("generic_name"));
            medication.setDrugClass(rs.getString("drug_class"));
            medication.setSubCategory(rs.getString("sub_category"));
            return medication;
        }
    }

    // RowMapper for MedicationList
    private static class MedicationListRowMapper implements RowMapper<MedicationList> {
        @Override
        public MedicationList mapRow(ResultSet rs, int rowNum) throws SQLException {
            MedicationList medList = new MedicationList();
            medList.setPatientId((UUID) rs.getObject("patient_id"));

            List<MedicationDTO> medicationDtoList = new ArrayList<>();
            ResultSet medicationRS = rs.getArray("medication_list").getResultSet();
            while (medicationRS.next()) {
                MedicationDTO med = new MedicationDTO();
                med.setBrandName(medicationRS.getString("brand_name"));
                med.setGenericName(medicationRS.getString("generic_name"));
                med.setDrugClass(DrugClass.valueOf(medicationRS.getString("drug_class")));
                med.setSubCategory(medicationRS.getString("sub_category"));
                med.setDosage(medicationRS.getString("dosage"));
                med.setFrequency(medicationRS.getString("frequency"));
                med.setRoute(medicationRS.getString("route"));
                med.setPrn(medicationRS.getBoolean("is_prn"));
                med.setDateStarted(medicationRS.getDate("date_started").toLocalDate());
                med.setCurrent(medicationRS.getBoolean("is_current"));
                med.setPharmacy(medicationRS.getString("pharmacy"));
                med.setComments(medicationRS.getString("comments"));
                medicationDtoList.add(med);
            }
            medList.setMedicationList(medicationDtoList);

            medList.setLastChanged(rs.getDate("last_changed").toLocalDate());
            return medList;
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
            medication.setDrugClass(rs.getString("drug_class"));
            medication.setSubCategory(rs.getString("sub_category"));

            // Set the MedicationId in MedicationInfo

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
