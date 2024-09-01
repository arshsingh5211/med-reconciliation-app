package com.arsh.controller;

import com.arsh.model.Medication;
import com.arsh.service.MedicationService;
import com.arsh.service.OpenFdaService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medications")
public class MedicationController {

    private final MedicationService medicationService;
    private final OpenFdaService openFdaService;


    @Autowired
    public MedicationController(MedicationService medicationService, OpenFdaService openFdaService) {
        this.medicationService = medicationService;
        this.openFdaService = openFdaService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<JsonNode> getMedicationInfo(@PathVariable String name) {
        JsonNode medicationInfo = openFdaService.getMedicationInfo(name);
        return ResponseEntity.ok(medicationInfo);
    }

    @GetMapping
    public List<Medication> getAllMedications() {
        return medicationService.getAllMedications();
    }

//    @GetMapping("/{id}")
//    public Medication getMedication(@PathVariable int id) {
//        return medicationService.getMedication(id);
//    }

    @PostMapping
    public void addMedication(@RequestBody Medication medication) {
        medicationService.saveMedication(medication);
    }

    @PutMapping("/{id}")
    public void updateMedication(@PathVariable int id, @RequestBody Medication medication) {
        // Ensure the medication ID matches the path variable ID
        if (medication.getMedicationId() == id) {
            medicationService.saveMedication(medication);
        } else {
            throw new IllegalArgumentException("Medication ID in the path does not match the ID in the request body.");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteMedication(@PathVariable int id) {
        medicationService.deleteMedication(id);
    }
}
