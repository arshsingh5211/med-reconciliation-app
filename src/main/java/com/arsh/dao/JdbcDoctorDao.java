package com.arsh.dao;

import com.arsh.model.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Repository
public class JdbcDoctorDao implements DoctorDao{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcDoctorDao (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Cacheable("doctorCache")
    public Doctor getDoctorById(UUID doctorId) {
        String sql = "SELECT * FROM doctor WHERE doctor_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{doctorId}, new DoctorRowMapper());
    }

    public class DoctorRowMapper implements RowMapper<Doctor> {
        @Override
        public Doctor mapRow(ResultSet rs, int rowNum) throws SQLException {
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

            return doctor;
        }
    }
}
