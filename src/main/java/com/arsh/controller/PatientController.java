package com.arsh.controller;

import com.arsh.dto.MedicationDTO;
import com.arsh.dto.PatientDTO;
import com.arsh.model.MedicationList;
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

    // todo: get specific medication info from a med list
    @GetMapping("/{patientId}/medications/{medicationInfoId}")
    public ResponseEntity<MedicationDTO> getMedicationForPatient(
            @PathVariable UUID patientId,
            @PathVariable int medicationInfoId) {
        MedicationDTO medicationDTO = medicationService.getMedicationOnMedList(medicationInfoId);
        return ResponseEntity.ok(medicationDTO);
    }

    @GetMapping("/{patientId}/medications")
    public ResponseEntity<MedicationList> getMedicationListByPatientId(@PathVariable UUID patientId) {
        return ResponseEntity.ok(medicationService.getMedicationListByPatientId(patientId));
    }

    @PostMapping("/{patientId}/medications")
    public ResponseEntity<Void> addMedicationToPatientList(
            @PathVariable UUID patientId,
            @RequestBody MedicationDTO medicationDTO) {
    medicationService.saveMedicationToMedicationList(patientId, medicationDTO);
        return ResponseEntity.ok().build();
    }

    // todo: delete med from med list
    // todo: update med on med list

    @PutMapping("/{patientId}/medications/{medicationInfoId}")
    public ResponseEntity<Void> updateMedicationInfo(
            @PathVariable UUID patientId,
            @PathVariable int medicationInfoId,
            @RequestBody MedicationDTO updatedInfo) {
        medicationService.updateMedicationOnMedList(medicationInfoId, updatedInfo);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{patientId}/medications/{medicationInfoId}")
    public ResponseEntity<Void> deleteMedicationInfo(@PathVariable UUID patientId, @PathVariable int medicationInfoId) {
        medicationService.deleteMedicationFromMedList(medicationInfoId);
        return ResponseEntity.ok().build();
    }
}