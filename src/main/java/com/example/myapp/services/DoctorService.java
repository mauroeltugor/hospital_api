package com.example.myapp.services;

import com.example.myapp.entity.Doctor;
import com.example.myapp.entity.DoctorSchedule;
import com.example.myapp.entity.DoctorSpecialty;
import com.example.myapp.entity.DoctorSpecialty.ExperienceLevel;
import com.example.myapp.entity.Specialty;
import com.example.myapp.repository.DoctorRepository;
import com.example.myapp.repository.SpecialtyRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import java.util.Set;

@Service

public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;

    public DoctorService(DoctorRepository doctorRepository, SpecialtyRepository specialtyRepository) {
        this.doctorRepository = doctorRepository;
        this.specialtyRepository = specialtyRepository;
    }

    public List<Doctor> getAll() {
        return doctorRepository.findAll();
    }

    public Doctor getById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doctor no encontrado con id: " + id));
    }

    public Doctor create(Doctor doctor) {
        if (doctorRepository.existsByLicenseNumber(doctor.getLicenseNumber())) {
            throw new RuntimeException("Ya existe un doctor con esa licencia");
        }
        return doctorRepository.save(doctor);
    }

    public Doctor updateDoctor(Long id, Doctor doctor) {
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doctor no encontrado con id: " + id));

        // Campos específicos de Doctor
        existing.setLicenseNumber(doctor.getLicenseNumber());
        existing.setSchedules(doctor.getSchedules());
        existing.setMedicalRecordItems(doctor.getMedicalRecordItems());
        existing.setPrescriptions(doctor.getPrescriptions());
        existing.setSpecialties(doctor.getSpecialties());

        // Campos heredados de User
        existing.setFirstName(doctor.getFirstName());
        existing.setLastName(doctor.getLastName());
        existing.setCc(doctor.getCc());
        existing.setUsername(doctor.getUsername());
        existing.setPhone(doctor.getPhone());
        existing.setPasswordHash(doctor.getPasswordHash());

        return doctorRepository.save(existing);
    }

    public void delete(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new EntityNotFoundException("Doctor no encontrado con id: " + id);
        }
        doctorRepository.deleteById(id);
    }

    @Transactional
    public String registerDoctor(Doctor doctor, Set<Long> specialtyIds) {
        if (doctorRepository.existsByLicenseNumber(doctor.getLicenseNumber())) {
            return "Número de licencia ya registrado.";
        }

        Set<DoctorSpecialty> doctorSpecialties = new HashSet<>();
        for (Long specialtyId : specialtyIds) {
            Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada: " + specialtyId));

            DoctorSpecialty ds = DoctorSpecialty.builder()
                    .doctor(doctor)
                    .specialty(specialty)
                    .experienceLevel(ExperienceLevel.INTERN) // o el valor que desees
                    .certificationDate(LocalDate.now()) // o la fecha que corresponda
                    .build();

            doctorSpecialties.add(ds);
        }

        doctor.setSpecialties(doctorSpecialties);
        doctorRepository.save(doctor);

        return "Doctor registrado exitosamente con especialidades.";
    }

    @Transactional
    public Doctor updateDoctor(Long userId, Doctor updatedData, Set<Long> newSpecialtyIds) {
        Doctor existing = doctorRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Doctor no encontrado con ID " + userId));

        // Actualizar campos básicos de contacto
        existing.setPhone(updatedData.getPhone());
        existing.setFirstName(updatedData.getFirstName());
        existing.setLastName(updatedData.getLastName());
        existing.setLicenseNumber(updatedData.getLicenseNumber());

        // Limpiar especialidades actuales y asignar nuevas
        existing.getSpecialties().clear();
        for (Long specialtyId : newSpecialtyIds) {
            Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con ID " + specialtyId));

            DoctorSpecialty ds = DoctorSpecialty.builder()
                    .doctor(existing)
                    .specialty(specialty)
                    .experienceLevel(ExperienceLevel.SENIOR) // o lo que se obtenga desde el frontend
                    .certificationDate(LocalDate.now())
                    .build();

            existing.getSpecialties().add(ds);
        }

        // Si necesitas actualizar disponibilidad, aquí puedes procesar `DoctorSchedule`

        return doctorRepository.save(existing);
    }

    @Transactional
    public List<DoctorSchedule> updateSchedules(Long doctorId, List<DoctorSchedule> newSchedules) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new EntityNotFoundException("Doctor no encontrado con ID " + doctorId));

        // Eliminar horarios antiguos si se desea reemplazar por completo
        //doctor.getSchedules().clear();

        // Validación simple: evitar solapamiento de horarios por work_day
        Set<String> seenDays = new HashSet<>();
        for (DoctorSchedule schedule : newSchedules) {
            if (seenDays.contains(schedule.getWorkDay().name())) {
                throw new IllegalArgumentException("Conflicto de horario: duplicado en " + schedule.getWorkDay());
            }
            seenDays.add(schedule.getWorkDay().name());

            schedule.setDoctor(doctor);
            doctor.getSchedules().add(schedule);
        }

        doctorRepository.save(doctor);
        return new ArrayList<>(doctor.getSchedules());
    }

}


