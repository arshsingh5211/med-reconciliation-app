package com.arsh.controller;

import com.arsh.model.Medication;
import com.arsh.service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medications-test")
public class MedicationController {

    private final MedicationService medicationService;

    @Autowired
    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    @GetMapping
    public List<Medication> getAllMedications() {
        return medicationService.getAllMedications();
    }

    @GetMapping("/{id}")
    public Medication getMedication(@PathVariable int id) {
        return medicationService.getMedication(id);
    }

    @PostMapping
    public void addMedication(@RequestBody Medication medication) {
        medicationService.saveMedication(medication);
    }

    @DeleteMapping("/{id}")
    public void deleteMedication(@PathVariable int id) {
        medicationService.deleteMedication(id);
    }
}