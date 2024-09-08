package com.arsh.dao;

import com.arsh.dto.MedicationDTO;
import com.arsh.exception.DoctorNotFoundException;
import com.arsh.exception.MedicationNotFoundException;
import com.arsh.exception.PatientNotFoundException;
import com.arsh.model.Medication;
import com.arsh.model.MedicationList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
public class JdbcMedicationDao implements MedicationDao {

    private static final Logger logger = LoggerFactory.getLogger(JdbcMedicationDao.class);
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
        try {
            Medication medication = jdbcTemplate.queryForObject(sql, new MedicationRowMapper(), medicationId);
            logger.info("Successfully fetched medication with ID: {}", medicationId);
            return medication;
        } catch (EmptyResultDataAccessException e) {
            logger.error("Medication with ID {} not found: {}", medicationId, e.getMessage());
            throw new MedicationNotFoundException("Medication not found with ID: " + medicationId);
        } catch (Exception e) {
            logger.error("Error fetching medication with ID {}: {}", medicationId, e.getMessage());
            throw new RuntimeException("Failed to fetch medication", e);
        }
    }

    @Override
    public void saveMedication(Medication medication) {
        String sql = "INSERT INTO Medication (brand_name, generic_name, drug_class, sub_category) VALUES (?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, medication.getBrandName(), medication.getGenericName(), medication.getDrugClass(), medication.getSubCategory());
            logger.info("Successfully saved medication: {}", medication);
        } catch (Exception e) {
            logger.error("Error saving medication: {}", e.getMessage());
            throw new RuntimeException("Failed to save medication", e);
        }
    }

    @Override
    public void deleteMedication(int medicationId) {
        String sql = "DELETE FROM Medication WHERE medication_id = ?";
        try {
            jdbcTemplate.update(sql, medicationId);
            logger.info("Successfully deleted medication with ID: {}", medicationId);
        } catch (Exception e) {
            logger.error("Error deleting medication with ID {}: {}", medicationId, e.getMessage());
            throw new RuntimeException("Failed to delete medication", e);
        }
    }

    @Override
    public List<Medication> getAllMedications() {
        String sql = "SELECT * FROM Medication";
        try {
            List<Medication> medications = jdbcTemplate.query(sql, new MedicationRowMapper());
            logger.info("Successfully fetched all medications");
            return medications;
        } catch (Exception e) {
            logger.error("Error fetching medications: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch medications", e);
        }
    }


    // -------------------------------------------------------------------
    // Functional Methods for Manipulating Patient's Medication Lists
    @Override
    public MedicationList getMedicationListByPatientId(UUID patientId) {
        String sql = "SELECT ml.medication_list_id, ml.patient_id, mi.updated_at AS mi_updated_at, ml.updated_at, mi.*, m.*, d.* " +
                "FROM MedicationList ml " +
                "LEFT JOIN MedicationInfo mi ON ml.medication_list_id = mi.medication_list_id " +
                "LEFT JOIN Medication m ON mi.medication_id = m.medication_id " +
                "LEFT JOIN Doctor d ON mi.prescribing_doctor_id = d.doctor_id " +
                "WHERE ml.patient_id = ?";
        try {
            MedicationList medicationList = jdbcTemplate.queryForObject(sql, new MedicationListRowMapper(), patientId);
            logger.info("Successfully fetched medication list for patient ID: {}", patientId);
            return medicationList;
        } catch (EmptyResultDataAccessException e) {
            logger.error("Patient with ID {} not found: {}", patientId, e.getMessage());
            throw new PatientNotFoundException("Patient not found with ID: " + patientId);
        } catch (Exception e) {
            logger.error("Error fetching medication list for patient ID {}: {}", patientId, e.getMessage());
            throw new RuntimeException("Failed to fetch medication list", e);
        }
    }

    @Transactional
    @Override
    public void saveMedicationToMedList(UUID patientId, MedicationDTO medicationDTO) {
        try {
            Integer medListId = getMedicationListId(patientId);
            Integer medicationId = getOrInsertMedication(medicationDTO);
            insertMedicationInfo(medListId, medicationId, getDoctor(medicationDTO.getPrescribingDoctorId()), medicationDTO);
            logger.info("Successfully saved medication for patient ID: {}", patientId);
        } catch (Exception e) {
            logger.error("Error saving medication for patient ID {}: {}", patientId, e.getMessage());
            throw new RuntimeException("Failed to save medication to med list", e);
        }
    }

    // Helper method to get MedicationList ID
    private Integer getMedicationListId(UUID patientId) {
        String sql = "SELECT medication_list_id FROM MedicationList WHERE patient_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, patientId);
        } catch (EmptyResultDataAccessException e) {
            logger.error("Patient with ID {} not found: {}", patientId);
            throw new PatientNotFoundException("No patient found for patient ID: " + patientId);
        }
    }


    // Helper method to get or insert Medication
    private Integer getOrInsertMedication(MedicationDTO medicationDTO) {
        String medIdSql = "SELECT medication_id FROM Medication WHERE brand_name = ? OR generic_name = ?";
        try {
            return jdbcTemplate.queryForObject(medIdSql, Integer.class, medicationDTO.getBrandName(), medicationDTO.getGenericName());
        } catch (EmptyResultDataAccessException e) {
            String insertMedSql = "INSERT INTO Medication (brand_name, generic_name, drug_class, sub_category) VALUES (?, ?, ?, ?) RETURNING medication_id";
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
            logger.error("Doctor with ID {} not found", doctorId);
            throw new DoctorNotFoundException("Doctor not found for ID: " + doctorId);
        }
    }

    // Helper method to insert MedicationInfo
    private void insertMedicationInfo(Integer medListId, Integer medicationId, UUID doctorId, MedicationDTO medicationDTO) {
        String sql = "INSERT INTO MedicationInfo (medication_list_id, medication_id, dosage, frequency, route, " +
                "is_prn, date_started, is_current, prescribing_doctor_id, pharmacy, comments, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, medListId,
                    medicationId,
                    medicationDTO.getDosage(), medicationDTO.getFrequency(), medicationDTO.getRoute(),
                    medicationDTO.isPrn(), medicationDTO.getDateStarted(), medicationDTO.isCurrent(),
                    doctorId, medicationDTO.getPharmacy(), medicationDTO.getComments(), medicationDTO.getUpdatedAt());
            logger.info("Successfully inserted medication info");
        } catch (Exception e) {
            logger.error("Error inserting medication info: {}", e.getMessage());
            throw new RuntimeException("Failed to insert medication info", e);
        }
    }

    @Override
    public void updateMedicationOnMedList(MedicationDTO medicationDTO) {
        String sql = "UPDATE MedicationInfo " +
                     "SET dosage = ?, frequency = ?, route = ?, is_prn = ?, " +
                        "date_started = ?, is_current = ?, prescribing_doctor_id = ?, " +
                        "pharmacy = ?, comments = ?, updated_at = ? " +
                     "WHERE medication_info_id = ?";
        try {
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
            logger.info("Successfully updated medication info ID: {}", medicationDTO.getMedicationInfoId());
        } catch (Exception e) {
            logger.error("Error updating medication info ID {}: {}", medicationDTO.getMedicationInfoId(), e.getMessage());
            throw new RuntimeException("Failed to update medication info", e);
        }
    }


    @Override
    public void deleteMedicationFromMedList(int medicationInfoId) {
        String sql = "DELETE FROM MedicationInfo WHERE medication_info_id = ?";
        try {
            jdbcTemplate.update(sql, medicationInfoId);
            logger.info("Successfully deleted medication info ID: {}", medicationInfoId);
        } catch (Exception e) {
            logger.error("Error deleting medication info ID {}: {}", medicationInfoId, e.getMessage());
            throw new RuntimeException("Failed to delete medication info", e);
        }
    }

    @Override
    public MedicationDTO getMedicationFromMedListById(int medicationInfoId) {
        String sql = "SELECT mi.medication_info_id, m.medication_id, mi.prescribing_doctor_id, " +
                        "m.brand_name, m.generic_name, m.drug_class, m.sub_category, mi.dosage, " +
                        "mi.frequency, mi.route, mi.is_prn, mi.date_started, mi.is_current, " +
                        "d.doctor_id, mi.pharmacy, mi.comments, mi.updated_at " +
                     "FROM MedicationInfo mi " +
                        "JOIN Medication m ON mi.medication_id = m.medication_id " +
                        "LEFT JOIN Doctor d ON mi.prescribing_doctor_id = d.doctor_id " +
                     "WHERE mi.medication_info_id = ?";
        try {
            MedicationDTO medication = jdbcTemplate.queryForObject(sql, new MedicationDTORowMapper(), medicationInfoId);
            logger.info("Successfully fetched medication info ID: {}", medicationInfoId);
            return medication;
        } catch (EmptyResultDataAccessException e) {
            logger.error("Medication info ID {} not found: {}", medicationInfoId, e.getMessage());
            throw new NoSuchElementException("Medication info not found with ID: " + medicationInfoId);
        } catch (Exception e) {
            logger.error("Error fetching medication info ID {}: {}", medicationInfoId, e.getMessage());
            throw new RuntimeException("Failed to fetch medication info", e);
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
                med.setDateStarted(
                        rs.getDate("date_started") != null ? rs.getDate("date_started").toLocalDate() : null
                );
                med.setCurrent(rs.getBoolean("is_current"));
                med.setPrescribingDoctorId((UUID) rs.getObject("prescribing_doctor_id"));
                med.setPharmacy(rs.getString("pharmacy"));
                med.setComments(rs.getString("comments"));
                Timestamp timestamp = rs.getTimestamp("mi_updated_at");
                med.setUpdatedAt(timestamp != null ? timestamp.toLocalDateTime() : null);
//                med.setUpdatedAt(rs.getTimestamp("mi_updated_at").toLocalDateTime());
                medicationDtoList.add(med);
            } while (rs.next());

            medList.setMedicationList(medicationDtoList);
            return medList;
        }
    }
}
