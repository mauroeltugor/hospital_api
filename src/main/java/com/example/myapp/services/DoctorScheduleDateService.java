package com.example.myapp.services;

import com.example.myapp.entity.DoctorScheduleDate;
import com.example.myapp.repository.DoctorScheduleDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorScheduleDateService {

    private final DoctorScheduleDateRepository repository;

    public DoctorScheduleDate create(DoctorScheduleDate doctorScheduleDate) {
        return repository.save(doctorScheduleDate);
    }

    public List<DoctorScheduleDate> findAll() {
        return repository.findAll();
    }

    public DoctorScheduleDate findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DoctorScheduleDate no encontrada con id: " + id));
    }

    public DoctorScheduleDate update(Long id, DoctorScheduleDate updated) {
        DoctorScheduleDate existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DoctorScheduleDate no encontrada con id: " + id));

        existing.setDate(updated.getDate());
        existing.setStatus(updated.getStatus());
        existing.setNotes(updated.getNotes());

        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("DoctorScheduleDate no encontrada con id: " + id);
        }
        repository.deleteById(id);
    }
}


