package com.example.myapp.controller;

import com.example.myapp.entity.Specialty;
import com.example.myapp.services.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyService service;

    @PostMapping
    public ResponseEntity<Specialty> create(@RequestBody Specialty specialty) {
        return ResponseEntity.ok(service.create(specialty));
    }

    @GetMapping
    public ResponseEntity<List<Specialty>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Specialty> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Specialty> update(@PathVariable Long id, @RequestBody Specialty updated) {
        return ResponseEntity.ok(service.update(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}


