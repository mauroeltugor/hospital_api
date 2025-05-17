package com.example.myapp.services;

import com.example.myapp.entity.Doctor;
import com.example.myapp.repository.DoctorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorServiceTest {

    private DoctorRepository repository;
    private DoctorService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(DoctorRepository.class);
        service = new DoctorService(repository, null);
    }

    @Test
    void testCreate_WhenLicenseNumberExists() {
        Doctor doctor = new Doctor();
        doctor.setLicenseNumber("ABC123");

        when(repository.existsByLicenseNumber("ABC123")).thenReturn(true);

        Exception ex = assertThrows(RuntimeException.class, () -> service.create(doctor));
        assertEquals("Ya existe un doctor con esa licencia", ex.getMessage());
    }

    @Test
    void testCreate_Success() {
        Doctor doctor = new Doctor();
        doctor.setLicenseNumber("XYZ123");

        when(repository.existsByLicenseNumber("XYZ123")).thenReturn(false);
        when(repository.save(doctor)).thenReturn(doctor);

        Doctor result = service.create(doctor);

        assertEquals("XYZ123", result.getLicenseNumber());
        verify(repository).save(doctor);
    }

    @Test
    void testGetById_WhenExists() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(doctor));

        Doctor result = service.getById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetById_WhenNotExists() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(EntityNotFoundException.class, () -> service.getById(1L));
        assertTrue(ex.getMessage().contains("Doctor no encontrado"));
    }

    @Test
    void testDelete_WhenNotExists() {
        when(repository.existsById(1L)).thenReturn(false);

        Exception ex = assertThrows(EntityNotFoundException.class, () -> service.delete(1L));
        assertTrue(ex.getMessage().contains("Doctor no encontrado"));
    }

    @Test
    void testDelete_Success() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }
}

