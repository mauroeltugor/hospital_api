package com.example.myapp.services;

import com.example.myapp.entity.Specialty;
import com.example.myapp.repository.SpecialtyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private SpecialtyService specialtyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSpecialty() {
        Specialty specialty = Specialty.builder()
                .name("Cardiology")
                .description("Heart-related specialty")
                .build();

        when(specialtyRepository.save(specialty)).thenReturn(specialty);

        Specialty result = specialtyService.create(specialty);
        assertEquals("Cardiology", result.getName());
        verify(specialtyRepository, times(1)).save(specialty);
    }

    @Test
    void testFindAllSpecialties() {
        List<Specialty> specialties = List.of(
                Specialty.builder().name("Cardiology").build(),
                Specialty.builder().name("Neurology").build()
        );

        when(specialtyRepository.findAll()).thenReturn(specialties);

        List<Specialty> result = specialtyService.findAll();
        assertEquals(2, result.size());
        verify(specialtyRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdWhenExists() {
        Specialty specialty = Specialty.builder().id(1L).name("Cardiology").build();

        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));

        Specialty result = specialtyService.findById(1L);
        assertEquals(1L, result.getId());
        assertEquals("Cardiology", result.getName());
        verify(specialtyRepository, times(1)).findById(1L);
    }

@Test
void testFindByIdWhenNotExists() {
    when(specialtyRepository.findById(1L)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () -> {
        specialtyService.findById(1L);
    });

    assertTrue(exception.getMessage().contains("Specialty no encontrada"));
}


    @Test
    void testUpdateSpecialty() {
        Specialty existing = Specialty.builder().id(1L).name("Cardiology").description("Old").build();
        Specialty updated = Specialty.builder().name("Cardiology").description("Updated").build();

        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(specialtyRepository.save(existing)).thenReturn(existing);

        Specialty result = specialtyService.update(1L, updated);
        assertEquals("Updated", result.getDescription());
        verify(specialtyRepository).findById(1L);
        verify(specialtyRepository).save(existing);
    }

    @Test
    void testDeleteSpecialty() {
        Specialty specialty = Specialty.builder().id(1L).build();

        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));
        doNothing().when(specialtyRepository).delete(specialty);

        specialtyService.delete(1L);
        verify(specialtyRepository).findById(1L);
        verify(specialtyRepository).delete(specialty);
    }
}
