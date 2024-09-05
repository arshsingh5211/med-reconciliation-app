package com.arsh.dao;

import com.arsh.dto.MedicationDTO;
import com.arsh.exception.DoctorNotFoundException;
import com.arsh.exception.PatientNotFoundException;
import com.arsh.model.Medication;
import com.arsh.model.MedicationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        try {
            // First, query for the MedicationList ID based on the patient ID
            String medListIdSql = "SELECT medication_list_id FROM MedicationList WHERE patient_id = ?";
            Integer medListId = jdbcTemplate.queryForObject(medListIdSql, Integer.class, patientId);
            // Get updatedAt value
            String updatedAtSql = "SELECT updated_at FROM MedicationList WHERE patient_id = ?";
            LocalDateTime updatedAt = jdbcTemplate.queryForObject("SELECT updated_at FROM MedicationList WHERE patient_id = ?", LocalDateTime.class, patientId);
            // Then query for the MedicationList details
            String medListSql = "SELECT ml.medication_list_id, ml.patient_id, ml.updated_at, mi.*, m.*, d.* " +
                    "FROM MedicationList ml " +
                    "LEFT JOIN MedicationInfo mi ON ml.medication_list_id = mi.medication_list_id " +
                    "LEFT JOIN Medication m ON mi.medication_id = m.medication_id " +
                    "LEFT JOIN Doctor d ON mi.prescribing_doctor_id = d.doctor_id " +
                    "WHERE ml.patient_id = ?";

            List<MedicationDTO> medicationDtoList = jdbcTemplate.query(medListSql, new MedicationDTORowMapper(), patientId);

            MedicationList medList = new MedicationList();
            medList.setMedicationListId(medListId);
            medList.setPatientId(patientId);

            if (!medicationDtoList.isEmpty()) {
                medList.setMedicationList(medicationDtoList);
                medList.setUpdatedAt(updatedAt);
            } else {
                medList.setMedicationList(new ArrayList<>());
            }
            return medList;
        } catch (EmptyResultDataAccessException e) {
            throw new PatientNotFoundException("Patient ID " + patientId + " does not exist.");
        }
    }


    @Override
    public void saveMedicationToMedList(UUID patientId, MedicationDTO medicationDTO) {
        // Get the MedicationList ID for the patient
        Integer medListId = getMedicationListId(patientId);

        // Get or Insert Medication
        Integer medicationId = getOrInsertMedication(medicationDTO);

        // Insert MedicationInfo
        insertMedicationInfo(medListId, medicationId, getDoctor(medicationDTO.
                             getPrescribingDoctorId()), medicationDTO);
    }

    // Helper method to get MedicationList ID
    private Integer getMedicationListId(UUID patientId) {
        String sql = "SELECT medication_list_id FROM MedicationList WHERE patient_id = ?";
    try {
        return jdbcTemplate.queryForObject(sql, Integer.class, patientId);
    } catch (EmptyResultDataAccessException e) {
        throw new PatientNotFoundException("No patient found for patient ID: " + patientId);
    }
}


    // Helper method to get or insert Medication
    private Integer getOrInsertMedication(MedicationDTO medicationDTO) {
        String medIdSql = "SELECT medication_id FROM Medication WHERE brand_name = ? OR generic_name = ?";
        try {
            return jdbcTemplate.queryForObject(medIdSql, Integer.class, medicationDTO.getBrandName(), medicationDTO.getGenericName());
        } catch (EmptyResultDataAccessException e) {
        // Insert the medication if not found
            String insertMedSql = "INSERT INTO Medication (brand_name, generic_name, drug_class, sub_category) " +
                    "VALUES (?, ?, ?, ?) RETURNING medication_id";
            return jdbcTemplate.queryForObject(insertMedSql, Integer.class,
                    medicationDTO.getBrandName(), medicationDTO.getGenericName(),
                    medicationDTO.getDrugClass(), medicationDTO.getSubCategory());
        }
    }

    private UUID getDoctor(UUID doctorId) {
        String sql = "SELECT doctor_id FROM Doctor WHERE doctor_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, UUID.class, doctorId);
        } catch (EmptyResultDataAccessException e) {
            throw new DoctorNotFoundException("Doctor not found for ID: " + doctorId);
        }
    }

    // Helper method to insert MedicationInfo
    private void insertMedicationInfo(Integer medListId, Integer medicationId, UUID doctorId, MedicationDTO medicationDTO) {
        String sql = "INSERT INTO MedicationInfo (medication_list_id, medication_id, dosage, frequency, route, " +
                "is_prn, date_started, is_current, prescribing_doctor_id, pharmacy, comments, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, medListId,
                medicationId,
                medicationDTO.getDosage(), medicationDTO.getFrequency(), medicationDTO.getRoute(),
                medicationDTO.isPrn(), medicationDTO.getDateStarted(), medicationDTO.isCurrent(),
                doctorId, medicationDTO.getPharmacy(), medicationDTO.getComments(), medicationDTO.getUpdatedAt());
    }

    @Override
    public void updateMedicationOnMedList(MedicationDTO medicationDTO) {
        String sql = "UPDATE MedicationInfo " +
                     "SET dosage = ?, frequency = ?, route = ?, is_prn = ?, " +
                        "date_started = ?, is_current = ?, prescribing_doctor_id = ?, " +
                        "pharmacy = ?, comments = ?, updated_at " +
                     "WHERE medication_info_id = ?";
        jdbcTemplate.update(sql,
                medicationDTO.getDosage(),
                medicationDTO.getFrequency(),
                medicationDTO.getRoute(),
                medicationDTO.isPrn(),
                medicationDTO.getDateStarted(),
                medicationDTO.isCurrent(),
                medicationDTO.getPrescribingDoctorId(),
                medicationDTO.getPharmacy(),
                medicationDTO.getComments(),
                medicationDTO.getUpdatedAt(),
                medicationDTO.getMedicationInfoId()
        );
    }


    @Override
    public void deleteMedicationFromMedList(int medicationInfoId) {
        String sql = "DELETE FROM MedicationInfo WHERE medication_info_id = ?";
        jdbcTemplate.update(sql, medicationInfoId);
    }

    @Override
    public MedicationDTO getMedicationFromMedListById(int medicationInfoId) {
    String sql = "SELECT mi.medication_info_id, m.medication_id, mi.prescribing_doctor_id, m.brand_name, m.generic_name, " +
                    "m.drug_class, m.sub_category, mi.dosage, mi.frequency, mi.route, mi.is_prn, " +
                    "mi.date_started, mi.is_current, d.doctor_id, " +
                    "mi.pharmacy, mi.comments, mi.updated_at " +
                 "FROM MedicationInfo mi " +
                    "JOIN Medication m ON mi.medication_id = m.medication_id " +
                    "LEFT JOIN Doctor d ON mi.prescribing_doctor_id = d.doctor_id " +
                "WHERE mi.medication_info_id = ?";
        return jdbcTemplate.queryForObject(sql, new MedicationDTORowMapper(), medicationInfoId);
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

    // RowMapper for MedicationDTO
    private static class MedicationDTORowMapper implements RowMapper<MedicationDTO> {
        @Override
        public MedicationDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            MedicationDTO medicationDTO = new MedicationDTO();
            medicationDTO.setMedicationInfoId(rs.getInt("medication_info_id"));
            medicationDTO.setBrandName(rs.getString("brand_name"));
            medicationDTO.setGenericName(rs.getString("generic_name"));
            medicationDTO.setDrugClass(rs.getString("drug_class"));
            medicationDTO.setSubCategory(rs.getString("sub_category"));
            medicationDTO.setDosage(rs.getString("dosage"));
            medicationDTO.setFrequency(rs.getString("frequency"));
            medicationDTO.setRoute(rs.getString("route"));
            medicationDTO.setPrn(rs.getBoolean("is_prn"));
            medicationDTO.setDateStarted(
                    rs.getDate("date_started") != null ? rs.getDate("date_started").toLocalDate() : LocalDate.now()
            );
            medicationDTO.setCurrent(rs.getBoolean("is_current"));
            // Handle optional prescribing doctor
            medicationDTO.setPrescribingDoctorId((UUID) rs.getObject("prescribing_doctor_id"));
            medicationDTO.setPharmacy(rs.getString("pharmacy"));
            medicationDTO.setComments(rs.getString("comments"));
            medicationDTO.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return medicationDTO;
        }
    }

    // RowMapper for MedicationList
    private static class MedicationListRowMapper implements RowMapper<MedicationList> {
        @Override
        public MedicationList mapRow(ResultSet rs, int rowNum) throws SQLException {
            MedicationList medList = new MedicationList();
            medList.setMedicationListId(rs.getInt("medication_list_id"));
            medList.setPatientId(rs.getObject("patient_id", UUID.class));
            medList.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

            List<MedicationDTO> medicationDtoList = new ArrayList<>();
            do {
                MedicationDTO med = new MedicationDTO();
                med.setMedicationInfoId(rs.getInt("medication_info_id"));
                med.setBrandName(rs.getString("brand_name"));
                med.setGenericName(rs.getString("generic_name"));
                med.setDrugClass(rs.getString("drug_class"));
                med.setSubCategory(rs.getString("sub_category"));
                med.setDosage(rs.getString("dosage"));
                med.setFrequency(rs.getString("frequency"));
                med.setRoute(rs.getString("route"));
                med.setPrn(rs.getBoolean("is_prn"));
                med.setDateStarted(rs.getDate("date_started").toLocalDate());
                med.setCurrent(rs.getBoolean("is_current"));
                med.setPrescribingDoctorId((UUID) rs.getObject("prescribing_doctor_id"));
                med.setPharmacy(rs.getString("pharmacy"));
                med.setComments(rs.getString("comments"));
                medicationDtoList.add(med);
            } while (rs.next());

            medList.setMedicationList(medicationDtoList);
            return medList;
        }
    }
}
