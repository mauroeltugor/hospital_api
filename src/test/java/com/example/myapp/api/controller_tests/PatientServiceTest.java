package com.example.myapp.api.controller_tests;

import com.example.myapp.entity.Patient;
import com.example.myapp.entity.Prescription;
import com.example.myapp.entity.Specialty;
import com.example.myapp.entity.Appointment.Status;
import com.example.myapp.entity.Appointment;
import com.example.myapp.entity.DoctorSchedule;
import com.example.myapp.entity.MedicalRecord;
import com.example.myapp.repository.PatientRepository;
import com.example.myapp.services.PatientService;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

public static Set<Appointment> createAppointments() {
        Patient patient1 = new Patient();
        patient1.setId((long) 1);
        patient1.setFirstName("Juan Pérez");
        patient1.setCc("12345678");
        patient1.setPhone("555-1234");

        Patient patient2 = new Patient();
        patient2.setId((long) 2);
        patient2.setFirstName("María García");
        patient2.setCc("87654321");
        patient2.setPhone("555-5678");

        // Crear especialidades
        Specialty specialty1 = new Specialty();
        specialty1.setId((long) 1);
        specialty1.setName("Cardiología");

        Specialty specialty2 = new Specialty();
        specialty2.setId((long) 2);
        specialty2.setName("Dermatología");

        // Crear horarios de doctores
        DoctorSchedule schedule1 = new DoctorSchedule();
        schedule1.setId((long) 1);
       

        DoctorSchedule schedule2 = new DoctorSchedule();
        schedule2.setId((long) 2);
    
        Appointment appointment1 = Appointment.builder()
                .status(Status.SCHEDULED)
                .effectiveness(90)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .patient(patient1)
                .schedule(schedule1)
                .specialty(specialty1)
                .build();

        Appointment appointment2 = Appointment.builder()
                .status(Status.CONFIRMED)
                .effectiveness(85)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .patient(patient2)
                .schedule(schedule2)
                .specialty(specialty2)
                .build();

        Set<Appointment> appointments = new HashSet<>();
        appointments.add(appointment1);
        appointments.add(appointment2);

        return appointments;
    }
    
    @Test
    void registerNewPatient_success() {
        Set<Appointment> newApp = createAppointments();
  
        Patient patient = new Patient();
        patient.setUsername("new_user");
        patient.setAppointments(newApp);
  
        when(patientRepository.existsByUsername("new_user")).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        String result = patientService.registerNewPatient(patient);

        assertTrue(result.contains("exitosamente"));
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void registerNewPatient_conflict() {
        Patient patient = new Patient();
        patient.setUsername("existing_user");

        when(patientRepository.existsByUsername("existing_user")).thenReturn(true);

        String result = patientService.registerNewPatient(patient);

        assertEquals("El nombre de usuario ya está registrado.", result);
        verify(patientRepository, never()).save(any());
    }

    @Test
    void getPatients_noFilter() {
        Page<Patient> mockPage = new PageImpl<>(List.of(new Patient()));
        when(patientRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<Patient> result = patientService.getPatients(null, 0, 10, "username", "ASC");

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getPatients_withFilter() {
        Page<Patient> mockPage = new PageImpl<>(List.of(new Patient()));
        when(patientRepository.findByUsernameContainingOrUserIdContaining(eq("john"), any(Pageable.class)))
            .thenReturn(mockPage);

        Page<Patient> result = patientService.getPatients("john", 0, 10, "username", "ASC");

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updatePatient_success() {
        Patient existing = new Patient();
        existing.setFirstName("Old");

        Patient updated = new Patient();
        updated.setFirstName("New");

        when(patientRepository.findById(1)).thenReturn(Optional.of(existing));
        when(patientRepository.save(existing)).thenReturn(existing);

        Patient result = patientService.updatePatient("1", updated);

        assertEquals("New", result.getFirstName());
    }

    @Test
    void updatePatient_notFound() {
        when(patientRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            patientService.updatePatient("999", new Patient());
        });
    }
}
