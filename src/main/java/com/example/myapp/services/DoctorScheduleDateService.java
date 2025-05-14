package com.example.myapp.services;

import com.example.myapp.entity.DoctorScheduleDate;
import com.example.myapp.repository.DoctorScheduleDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorScheduleDateService {

    @Autowired
    private DoctorScheduleDateRepository doctorScheduleDateRepository;

    public DoctorScheduleDate createDoctorScheduleDate(DoctorScheduleDate doctorScheduleDate) {
        return doctorScheduleDateRepository.save(doctorScheduleDate);
    }

    public List<DoctorScheduleDate> getAllDoctorScheduleDates() {
        return doctorScheduleDateRepository.findAll();
    }

    public Optional<DoctorScheduleDate> getDoctorScheduleDateById(Long id) {
        return doctorScheduleDateRepository.findById(id);
    }

    public DoctorScheduleDate updateDoctorScheduleDate(Long id, DoctorScheduleDate doctorScheduleDate) {
        return doctorScheduleDateRepository.findById(id).map(existingScheduleDate -> {
            existingScheduleDate.setDate(doctorScheduleDate.getDate());
            existingScheduleDate.setStatus(doctorScheduleDate.getStatus());
            existingScheduleDate.setNotes(doctorScheduleDate.getNotes());
            return doctorScheduleDateRepository.save(existingScheduleDate);
        }).orElseThrow(() -> new RuntimeException("DoctorScheduleDate not found"));
    }

    public void deleteDoctorScheduleDate(Long id) {
        doctorScheduleDateRepository.deleteById(id);
    }
}

