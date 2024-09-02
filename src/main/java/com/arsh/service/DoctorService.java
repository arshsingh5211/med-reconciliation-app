package com.arsh.service;

import com.arsh.dao.DoctorDao;
import com.arsh.model.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DoctorService {

    private final DoctorDao doctorDao;

    @Autowired
    public DoctorService(DoctorDao doctorDao) {
        this.doctorDao = doctorDao;
    }

    public Doctor getDoctorById(UUID doctorId) {
        return doctorDao.getDoctorById(doctorId);
    }
}
