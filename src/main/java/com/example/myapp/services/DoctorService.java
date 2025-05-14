package com.example.myapp.services;

import com.example.myapp.entity.Doctor;
import com.example.myapp.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<Doctor> getAll() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> getById(Long id) {
        return doctorRepository.findById(id);
    }

    public Doctor create(Doctor doctor) {
        if (doctorRepository.existsByLicenseNumber(doctor.getLicenseNumber())) {
            throw new RuntimeException("Ya existe un doctor con esa licencia");
        }
        return doctorRepository.save(doctor);
    }

public Doctor updateDoctor(Long id, Doctor doctor) {
    return doctorRepository.findById(id).map(d -> {
        // Campos propios de Doctor
        d.setLicenseNumber(doctor.getLicenseNumber());
        d.setSchedules(doctor.getSchedules());
        d.setMedicalRecordItems(doctor.getMedicalRecordItems());
        d.setPrescriptions(doctor.getPrescriptions());
        d.setSpecialties(doctor.getSpecialties());

        // ✅ Campos heredados de User
        d.setFirstName(doctor.getFirstName());
        d.setLastName(doctor.getLastName());
        d.setCc(doctor.getCc());
        d.setUsername(doctor.getUsername());
        d.setPhone(doctor.getPhone());
        d.setPasswordHash(doctor.getPasswordHash());

        return doctorRepository.save(d);
    }).orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
}


    public void eliminar(Long id) {
        doctorRepository.deleteById(id);
    }
}

