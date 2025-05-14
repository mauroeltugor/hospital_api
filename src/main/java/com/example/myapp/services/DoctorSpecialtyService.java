package com.example.myapp.services;

import com.example.myapp.entity.DoctorSpecialty;
import com.example.myapp.repository.DoctorSpecialtyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorSpecialtyService {

    private final DoctorSpecialtyRepository doctorSpecialtyRepository;

    public DoctorSpecialty create(DoctorSpecialty doctorSpecialty) {
        return doctorSpecialtyRepository.save(doctorSpecialty);
    }

    public List<DoctorSpecialty> findAll() {
        return doctorSpecialtyRepository.findAll();
    }

    public DoctorSpecialty findById(Long id) {
        return doctorSpecialtyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DoctorSpecialty no encontrada con id: " + id));
    }

    public DoctorSpecialty update(Long id, DoctorSpecialty updated) {
        return doctorSpecialtyRepository.findById(id)
                .map(existing -> {
                    existing.setDoctor(updated.getDoctor());
                    existing.setSpecialty(updated.getSpecialty());
                    existing.setCertificationDate(updated.getCertificationDate());
                    existing.setExperienceLevel(updated.getExperienceLevel());
                    return doctorSpecialtyRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("DoctorSpecialty no encontrada con id: " + id));
    }

    public void delete(Long id) {
        doctorSpecialtyRepository.deleteById(id);
    }
}

