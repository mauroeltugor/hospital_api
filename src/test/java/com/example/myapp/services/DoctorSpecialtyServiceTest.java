package com.example.myapp.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import com.example.myapp.entity.DoctorSpecialty;
import com.example.myapp.entity.DoctorSpecialty.ExperienceLevel;
import com.example.myapp.repository.DoctorSpecialtyRepository;


public class DoctorSpecialtyServiceTest {

    @Mock
    private DoctorSpecialtyRepository repository;

    private DoctorSpecialtyService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new DoctorSpecialtyService(repository);
    }

    @Test
    void testFindByIdWhenExists() {
        DoctorSpecialty ds = DoctorSpecialty.builder()
            .id(1L)
            .certificationDate(LocalDate.of(2020, 1, 1))
            .experienceLevel(ExperienceLevel.JUNIOR)
            .build();

        when(repository.findById(1L)).thenReturn(Optional.of(ds));

        DoctorSpecialty found = service.findById(1L);
        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void testFindByIdWhenNotExists() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.findById(1L);
        });

        assertTrue(exception.getMessage().contains("DoctorSpecialty no encontrada"));
    }

    @Test
    void testUpdateWhenExists() {
        DoctorSpecialty existing = DoctorSpecialty.builder()
            .id(1L)
            .certificationDate(LocalDate.of(2020, 1, 1))
            .experienceLevel(ExperienceLevel.JUNIOR)
            .build();

        DoctorSpecialty updated = DoctorSpecialty.builder()
            .certificationDate(LocalDate.of(2022, 1, 1))
            .experienceLevel(ExperienceLevel.SENIOR)
            .build();

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(DoctorSpecialty.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DoctorSpecialty result = service.update(1L, updated);

        assertEquals(ExperienceLevel.SENIOR, result.getExperienceLevel());
        assertEquals(LocalDate.of(2022, 1, 1), result.getCertificationDate());
        assertEquals(1L, result.getId());
    }

    @Test
    void testUpdateWhenNotExists() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        DoctorSpecialty updated = new DoctorSpecialty();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.update(1L, updated);
        });

        assertTrue(exception.getMessage().contains("DoctorSpecialty no encontrada"));
    }

    @Test
    void testDeleteWhenExists() {
        DoctorSpecialty existing = DoctorSpecialty.builder()
            .id(1L)
            .certificationDate(LocalDate.of(2020, 1, 1))
            .experienceLevel(ExperienceLevel.JUNIOR)
            .build();

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(repository).delete(existing);

        assertDoesNotThrow(() -> service.delete(1L));

        verify(repository, times(1)).delete(existing);
    }

    @Test
    void testDeleteWhenNotExists() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.delete(1L);
        });

        assertTrue(exception.getMessage().contains("DoctorSpecialty no encontrada"));
    }

    @Test
    void testCreate() {
        DoctorSpecialty ds = DoctorSpecialty.builder()
            .certificationDate(LocalDate.of(2021, 6, 15))
            .experienceLevel(ExperienceLevel.MID)
            .build();

        when(repository.save(ds)).thenReturn(ds);

        DoctorSpecialty created = service.create(ds);

        assertNotNull(created);
        assertEquals(ExperienceLevel.MID, created.getExperienceLevel());
    }

    @Test
    void testFindAll() {
        List<DoctorSpecialty> list = Arrays.asList(
            DoctorSpecialty.builder().id(1L).build(),
            DoctorSpecialty.builder().id(2L).build()
        );

        when(repository.findAll()).thenReturn(list);

        List<DoctorSpecialty> result = service.findAll();

        assertEquals(2, result.size());
    }
}


