package com.example.myapp.controller;

import com.example.myapp.entity.Patient;
import com.example.myapp.services.PatientService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;


@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerPatient(@RequestBody Patient patient) {
        String result = patientService.registerNewPatient(patient);
        if (result.contains("successfully")) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
    }

    @GetMapping("/listPatient")
    public ResponseEntity<Page<Patient>> listPatients(
            @RequestParam(required = false) String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Page<Patient> patients = patientService.getPatients(filter, page, size, sortBy, direction);
        return ResponseEntity.ok(patients);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Patient> editPatient(
            @PathVariable String userId,
            @RequestBody Patient updatedPatient) {
        try {
            Patient patient = patientService.updatePatient(userId, updatedPatient);
            return ResponseEntity.ok(patient);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
