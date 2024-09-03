package com.arsh.controller;

import com.arsh.dto.PatientDTO;
import com.arsh.model.MedicationInfo;
import com.arsh.service.MedicationService;
import com.arsh.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final MedicationService medicationService;

    @Autowired
    public PatientController(PatientService patientService, MedicationService medicationService) {
        this.patientService = patientService;
        this.medicationService = medicationService;
    }

    // Patient CRUD endpoints

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientDTO> getPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(patientService.getPatient(patientId));
    }

    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @PostMapping
    public ResponseEntity<Void> savePatient(@RequestBody PatientDTO patientDTO) {
        patientService.savePatient(patientDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{patientId}")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID patientId) {
        patientService.deletePatient(patientId);
        return ResponseEntity.ok().build();
    }

    // Medication CRUD endpoints for a specific patient

    @GetMapping("/{patientId}/medications")
    public ResponseEntity<List<MedicationInfo>> getMedicationListByPatientId(@PathVariable UUID patientId) {
        return ResponseEntity.ok(medicationService.getMedicationListByPatientId(patientId));
    }

    @PostMapping("/{patientId}/medications")
    public ResponseEntity<Void> addMedicationToPatientList(
            @PathVariable UUID patientId,
            @RequestBody MedicationInfo medicationInfo) {
        medicationInfo.setPatientId(patientId);
//        medicationService.addMedication(medicationInfo);
        return ResponseEntity.ok().build();
    }

//    @PutMapping("/{patientId}/medications/{medicationInfoId}")
//    public ResponseEntity<Void> updateMedicationInfo(@PathVariable UUID patientId, @PathVariable int medicationInfoId, @RequestBody MedicationInfo updatedInfo) {
//        medicationService.updateMedicationInfo(medicationInfoId, updatedInfo);
//        return ResponseEntity.ok().build();
//    }

//    @DeleteMapping("/{patientId}/medications/{medicationInfoId}")
//    public ResponseEntity<Void> deleteMedicationInfo(@PathVariable UUID patientId, @PathVariable int medicationInfoId) {
//        medicationService.deleteMedicationInfo(medicationInfoId);
//        return ResponseEntity.ok().build();
//    }
}