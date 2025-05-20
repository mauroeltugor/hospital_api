package com.example.myapp.services;

import com.example.myapp.entity.Patient;
import com.example.myapp.entity.MedicalRecord;
import com.example.myapp.repository.PatientRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public String registerNewPatient(Patient patient) {
        if (patientRepository.existsByUsername(patient.getUsername())) {
            return "The username is already registered.";
        }

        // Crear historial médico vacío
        MedicalRecord medicalRecord = MedicalRecord.builder()
                .patient(patient)  // relación bidireccional
                .build();

        patient.setMedicalRecord(medicalRecord);

        // Asegurar otras relaciones (si se usan)
        patient.getAppointments().forEach(a -> a.setPatient(patient));
        patient.getPrescriptions().forEach(p -> p.setPatient(patient));
        patient.getSessions().forEach(s -> s.setPatient(patient));
        patient.getAllergies().forEach(a -> a.setPatient(patient));

        // Guardar el paciente junto con su historial médico
        patientRepository.save(patient);

        return "Patient successfully registered with medical history.";
    }

     public Page<Patient> getPatients(String filter, int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        PageRequest pageable = PageRequest.of(page, size, sort);

        if (filter == null || filter.isBlank()) {
            return patientRepository.findAll(pageable);
        }
        return patientRepository.findByUsernameContainingOrUserIdContaining(filter, pageable);
    }
    
    @Transactional
    public Patient updatePatient(String userId, Patient updatedPatient) {
        Patient existingPatient = patientRepository.findById(Integer.parseInt(userId))
            .orElseThrow(() -> new EntityNotFoundException("Patient not found with id " + userId));

        // Actualizar campos básicos (ajusta según campos reales)
        existingPatient.setFirstName(updatedPatient.getFirstName());
        existingPatient.setLastName(updatedPatient.getLastName());
        existingPatient.setPhone(updatedPatient.getPhone());
        existingPatient.setBirthDate(updatedPatient.getBirthDate());
        existingPatient.setGender(updatedPatient.getGender());
        existingPatient.setBloodType(updatedPatient.getBloodType());

        // Actualiza más campos si tienes en Patient o User (dirección, etc.)

        return patientRepository.save(existingPatient);
    }
}