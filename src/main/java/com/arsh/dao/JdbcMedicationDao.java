package com.arsh.dao;

import com.arsh.dto.MedicationDTO;
import com.arsh.model.Doctor;
import com.arsh.model.Medication;
import com.arsh.model.MedicationInfo;
import com.arsh.model.MedicationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
    // First, query for the MedicationList ID based on the patient ID
    String medListIdSql = "SELECT medication_list_id FROM MedicationList WHERE patient_id = ?";
    Integer medListId = jdbcTemplate.queryForObject(medListIdSql, Integer.class, patientId);

    // Then query for the MedicationList details
    String medListDetailsSql = "SELECT ml.medication_list_id, ml.patient_id, ml.last_changed, mi.*, m.*, d.* " +
                "FROM MedicationList ml " +
                "LEFT JOIN MedicationInfo mi ON ml.medication_list_id = mi.medication_list_id " +
                "LEFT JOIN Medication m ON mi.medication_id = m.medication_id " +
                "LEFT JOIN Doctor d ON mi.prescribing_doctor = d.doctor_id " +
            "WHERE ml.medication_list_id = ?";

    List<MedicationDTO> medicationDtoList = jdbcTemplate.query(medListDetailsSql, new MedicationDTORowMapper(), medListId);

        MedicationList medList = new MedicationList();
        medList.setMedicationListId(medListId);
        medList.setPatientId(patientId);

        if (!medicationDtoList.isEmpty()) {
            medList.setMedicationList(medicationDtoList);
            medList.setLastChanged(medicationDtoList.get(0).getLastChanged());
        } else {
            medList.setMedicationList(new ArrayList<>());
        }

        return medList;
    }


    @Override
    public void saveMedicationToMedList(UUID patientId, MedicationDTO medicationDTO) {
        // Get the MedicationList ID for the patient
        Integer medListId = getMedicationListId(patientId);

        // Get or Insert Medication
        Integer medicationId = getOrInsertMedication(medicationDTO);

        // Get or Insert Doctor
        UUID doctorId = getOrInsertDoctor(medicationDTO.getPrescribingDoctor());

        // Insert MedicationInfo
        insertMedicationInfo(medListId, medicationId, doctorId, medicationDTO);
    }

    // Helper method to get MedicationList ID
    private Integer getMedicationListId(UUID patientId) {
        String sql = "SELECT medication_list_id FROM MedicationList WHERE patient_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, patientId);
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

    // Helper method to get or insert Doctor
    private UUID getOrInsertDoctor(Doctor doctorDTO) {
        if (doctorDTO == null) return null;

        String docIdSql = "SELECT doctor_id FROM Doctor WHERE doctor_id = ?";
        try {
            return jdbcTemplate.queryForObject(docIdSql, UUID.class, doctorDTO.getDoctorId());
        } catch (EmptyResultDataAccessException e) {
        // Insert the doctor if not found
            String insertDocSql = "INSERT INTO Doctor (first_name, last_name, specialty, phone_number, " +
                "street_address, city, state, zip_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING doctor_id";
            return jdbcTemplate.queryForObject(insertDocSql, UUID.class,
                    doctorDTO.getFirstName(), doctorDTO.getLastName(),
                    doctorDTO.getSpecialty(), doctorDTO.getPhoneNumber(),
                    doctorDTO.getStreetAddress(), doctorDTO.getCity(),
                    doctorDTO.getState(), doctorDTO.getZipCode());
        }
    }


    // Helper method to insert MedicationInfo
    private void insertMedicationInfo(Integer medListId, Integer medicationId, UUID doctorId, MedicationDTO medicationDTO) {
        String sql = "INSERT INTO MedicationInfo (medication_list_id, medication_id, dosage, frequency, route, " +
                "is_prn, date_started, is_current, prescribing_doctor, pharmacy, comments) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, medListId,
                medicationId,
                medicationDTO.getDosage(), medicationDTO.getFrequency(), medicationDTO.getRoute(),
                medicationDTO.isPrn(), medicationDTO.getDateStarted(), medicationDTO.isCurrent(),
                doctorId, medicationDTO.getPharmacy(), medicationDTO.getComments());
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
                medicationDTO.getMedicationInfoId()
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
            UUID doctorId = (UUID) rs.getObject("prescribing_doctor");
            if (doctorId != null) {
                Doctor doctor = new Doctor();
                doctor.setDoctorId(doctorId);
                doctor.setFirstName(rs.getString("first_name"));
                doctor.setLastName(rs.getString("last_name"));
                medicationDTO.setPrescribingDoctor(doctor);
            }
            medicationDTO.setPharmacy(rs.getString("pharmacy"));
            medicationDTO.setComments(rs.getString("comments"));
            medicationDTO.setLastChanged(rs.getTimestamp("last_changed").toLocalDateTime());
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
            medList.setLastChanged(rs.getTimestamp("last_changed").toLocalDateTime());

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
                // Handle optional prescribing doctor
                UUID doctorId = (UUID) rs.getObject("prescribing_doctor");
                if (doctorId != null) {
                    Doctor doctor = new Doctor();
                    doctor.setDoctorId(doctorId);
                    doctor.setFirstName(rs.getString("first_name"));
                    doctor.setLastName(rs.getString("last_name"));
                    med.setPrescribingDoctor(doctor);
                }
                med.setPharmacy(rs.getString("pharmacy"));
                med.setComments(rs.getString("comments"));
                medicationDtoList.add(med);
            } while (rs.next());

            medList.setMedicationList(medicationDtoList);
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
