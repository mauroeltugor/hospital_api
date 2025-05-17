package com.example.myapp.services;

import com.example.myapp.entity.Specialty;
import com.example.myapp.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyService {

    private final SpecialtyRepository repository;

    public Specialty create(Specialty specialty) {
        if (repository.existsByName(specialty.getName())) {
            throw new IllegalArgumentException("Specialty with this name already exists.");
        }
        return repository.save(specialty);
    }

    public List<Specialty> findAll() {
        return repository.findAll();
    }

public Specialty findById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Specialty no encontrada con id: " + id));
}


    public Specialty update(Long id, Specialty updated) {
        Specialty existing = findById(id);

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());

        return repository.save(existing);
    }

    public void delete(Long id) {
        Specialty existing = findById(id);
        repository.delete(existing);
    }
}



