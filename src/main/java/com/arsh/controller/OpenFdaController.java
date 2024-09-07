package com.arsh.controller;

import com.arsh.service.OpenFdaService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medications")
public class OpenFdaController {
    private final OpenFdaService openFdaService;


    @Autowired
    public OpenFdaController(OpenFdaService openFdaService) {
        this.openFdaService = openFdaService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<JsonNode> getMedicationInfo(@PathVariable String name) {
        JsonNode medicationInfo = openFdaService.getMedicationInfo(name);
        return ResponseEntity.ok(medicationInfo);
    }
}
