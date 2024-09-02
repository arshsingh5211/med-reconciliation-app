package com.arsh.controller;

import com.arsh.model.Patient;
import com.arsh.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<Patient> getPatient(@PathVariable UUID patientId) {
        Patient patient = patientService.getPatient(patientId);
        if (patient != null) {
            return new ResponseEntity<>(patient, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> savePatient(@RequestBody Patient patient) {
        patientService.savePatient(patient);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePatient(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        patientService.updatePatient(id, updates);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{patientId}")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID patientId) {
        patientService.deletePatient(patientId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}