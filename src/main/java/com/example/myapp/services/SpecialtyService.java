package com.example.myapp.services;

import com.example.myapp.entity.Specialty;
import com.example.myapp.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public Specialty createSpecialty(Specialty specialty) {
        return specialtyRepository.save(specialty);
    }

    public List<Specialty> getAllSpecialties() {
        return specialtyRepository.findAll();
    }

    public Specialty getSpecialtyById(Long id) {
        return specialtyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialty not found"));
    }

    public Specialty updateSpecialty(Long id, Specialty updatedSpecialty) {
        return specialtyRepository.findById(id).map(s -> {
            s.setName(updatedSpecialty.getName());
            s.setDescription(updatedSpecialty.getDescription());
            return specialtyRepository.save(s);
        }).orElseThrow(() -> new RuntimeException("Specialty not found"));
    }

    public void deleteSpecialty(Long id) {
        specialtyRepository.deleteById(id);
    }
}

