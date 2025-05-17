package com.example.myapp.services;

import com.example.myapp.entity.DoctorSpecialty;
import com.example.myapp.repository.DoctorSpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorSpecialtyService {

    private final DoctorSpecialtyRepository repository;

    public DoctorSpecialty create(DoctorSpecialty doctorSpecialty) {
        return repository.save(doctorSpecialty);
    }

    public List<DoctorSpecialty> findAll() {
        return repository.findAll();
    }

public DoctorSpecialty findById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new RuntimeException("DoctorSpecialty no encontrada con id: " + id));
}

public DoctorSpecialty update(Long id, DoctorSpecialty updated) {
    DoctorSpecialty existing = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("DoctorSpecialty no encontrada con id: " + id));

    // Actualizar los campos que quieres modificar
    existing.setCertificationDate(updated.getCertificationDate());
    existing.setExperienceLevel(updated.getExperienceLevel());
    // Si hay más campos que actualizar, agrégalos aquí.

    return repository.save(existing);
}


    public void delete(Long id) {
    DoctorSpecialty existing = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("DoctorSpecialty no encontrada con id: " + id));
        repository.delete(existing);
    }
}


