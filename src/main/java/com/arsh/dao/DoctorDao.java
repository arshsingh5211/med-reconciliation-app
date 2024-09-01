package com.arsh.dao;

import com.arsh.model.Doctor;

import java.util.UUID;

public interface DoctorDao {
    Doctor getDoctorById(UUID doctorId);
}
