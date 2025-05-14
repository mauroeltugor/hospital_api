package com.example.myapp.controller;

import com.example.myapp.entity.DoctorSpecialty;
import com.example.myapp.services.DoctorSpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-specialties")
@RequiredArgsConstructor
public class DoctorSpecialtyController {

    private final DoctorSpecialtyService service;

    @PostMapping
    public ResponseEntity<DoctorSpecialty> create(@RequestBody DoctorSpecialty doctorSpecialty) {
        return ResponseEntity.ok(service.create(doctorSpecialty));
    }

    @GetMapping
    public ResponseEntity<List<DoctorSpecialty>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorSpecialty> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorSpecialty> update(@PathVariable Long id, @RequestBody DoctorSpecialty updated) {
        return ResponseEntity.ok(service.update(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

