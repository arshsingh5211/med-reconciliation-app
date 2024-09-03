package com.arsh.controller;

import com.arsh.dto.PatientDTO;
import com.arsh.model.Medication;
import com.arsh.service.MedicationListService;
import com.arsh.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final MedicationListService medicationListService;

    @Autowired
    public PatientController(PatientService patientService, MedicationListService medicationListService) {
        this.patientService = patientService;
        this.medicationListService = medicationListService;
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientDTO> getPatient(@PathVariable UUID patientId) {
        PatientDTO patientDTO = patientService.getPatient(patientId);
        if (patientDTO != null) {
            return new ResponseEntity<>(patientDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientService.getAllPatients();
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> savePatient(@RequestBody PatientDTO patientDTO) {
        patientService.savePatient(patientDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

//    @PatchMapping("/{id}")
//    public ResponseEntity<Void> updatePatient(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
//        patientService.updatePatient(id, updates);
//        return ResponseEntity.noContent().build();
//    }

    @DeleteMapping("/{patientId}")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID patientId) {
        patientService.deletePatient(patientId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{patientId}/medications")
    public ResponseEntity<List<Medication>> getMedicationsByPatientId(@PathVariable UUID patientId) {
        List<Medication> medications = medicationListService.getMedicationListByPatientId(patientId);
        if (medications.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(medications, HttpStatus.OK);
    }

    @PostMapping("/{patientId}/medications")
    public ResponseEntity<Void> addMedicationToPatientList(@PathVariable UUID patientId, @RequestBody Medication medication) {
        medicationListService.addMedicationToPatientList(patientId, medication);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}