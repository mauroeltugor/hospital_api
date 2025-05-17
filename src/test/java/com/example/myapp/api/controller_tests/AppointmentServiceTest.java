package com.example.myapp.api.controller_tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.myapp.entity.Appointment;
import com.example.myapp.entity.Doctor;
import com.example.myapp.entity.DoctorSchedule;
import com.example.myapp.entity.Patient;
import com.example.myapp.repository.AppointmentRepository;
import com.example.myapp.services.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    private Appointment appt1;
    private Appointment appt2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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

        // Primer Doctor
        Doctor doctor1 = new Doctor();
        doctor1.setUserId(1L);
        doctor1.setLicenseNumber("LIC12345");
        doctor1.setSchedules(new java.util.HashSet<>()); // vacío inicialmente
        doctor1.setMedicalRecordItems(new java.util.HashSet<>());
        doctor1.setPrescriptions(new java.util.HashSet<>());
        doctor1.setSpecialties(new java.util.HashSet<>());

        // Puedes también establecer propiedades heredadas si existen, por ejemplo:
        doctor1.setUsername("Dra. Ana Torres");
       
        // Segundo Doctor
        Doctor doctor2 = new Doctor();
        doctor2.setUserId(2L);
        doctor2.setLicenseNumber("LIC67890");
        doctor2.setSchedules(new java.util.HashSet<>());
        doctor2.setMedicalRecordItems(new java.util.HashSet<>());
        doctor2.setPrescriptions(new java.util.HashSet<>());
        doctor2.setSpecialties(new java.util.HashSet<>());

        doctor2.setUsername("Dr. Carlos Méndez");

          // Crear horarios de doctores
        DoctorSchedule schedule1 = new DoctorSchedule();
        schedule1.setId((long) 1);
        schedule1.setDoctor(doctor1);
       

        DoctorSchedule schedule2 = new DoctorSchedule();
        schedule2.setId((long) 2);
        schedule1.setDoctor(doctor2);

        appt1 = new Appointment();
        appt1.setId((long) 1);
        appt1.setPatient(patient1);
        appt1.setSchedule(schedule1);

        appt2 = new Appointment();
        appt2.setId((long) 2);
        appt2.setPatient(patient2);
        appt2.setSchedule(schedule2);
        
    }

   
    @Test
    void getAppointmentById_Found() throws Exception {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appt1));

        Optional<Appointment> result = appointmentService.getAppointmentById(1);
        verify(appointmentRepository).findById(1);
    }

    @Test
    void getAppointmentById_NotFound_Throws() {
        when(appointmentRepository.findById(99)).thenReturn(Optional.empty());

    }

    @Test
    void createAppointment_SavesAndReturns() {
        when(appointmentRepository.save(appt1)).thenReturn(appt1);

        Appointment result = appointmentService.createAppointment(appt1);
        assertEquals(appt1, result);
        verify(appointmentRepository).save(appt1);
    }

    @Test
    void deleteAppointment_Existing() throws Exception {
        when(appointmentRepository.existsById(1)).thenReturn(true);
        doNothing().when(appointmentRepository).deleteById(1);

        appointmentService.deleteAppointment(1);
        verify(appointmentRepository).deleteById(1);
    }

}
