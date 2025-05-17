package com.example.myapp.services;

import com.example.myapp.entity.DoctorScheduleDate;
import com.example.myapp.repository.DoctorScheduleDateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorScheduleDateServiceTest {

    @Mock
    private DoctorScheduleDateRepository repository;

    @InjectMocks
    private DoctorScheduleDateService service;

    private DoctorScheduleDate exampleScheduleDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        exampleScheduleDate = DoctorScheduleDate.builder()
                .id(1L)
                .date(LocalDate.of(2025, 5, 17))
                .status(DoctorScheduleDate.Status.ACTIVE)
                .notes("Morning hours")
                .build();
    }

    @Test
    void testCreate() {
        when(repository.save(exampleScheduleDate)).thenReturn(exampleScheduleDate);

        DoctorScheduleDate result = service.create(exampleScheduleDate);

        assertNotNull(result);
        assertEquals(exampleScheduleDate.getId(), result.getId());
        verify(repository).save(exampleScheduleDate);
    }

    @Test
    void testFindAll() {
        List<DoctorScheduleDate> list = List.of(exampleScheduleDate);
        when(repository.findAll()).thenReturn(list);

        List<DoctorScheduleDate> result = service.findAll();

        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    @Test
    void testFindByIdWhenExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(exampleScheduleDate));

        DoctorScheduleDate result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindByIdWhenNotExists() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.findById(1L));
        assertTrue(ex.getMessage().contains("DoctorScheduleDate no encontrada"));
    }

    @Test
    void testUpdateWhenExists() {
        DoctorScheduleDate updated = DoctorScheduleDate.builder()
                .date(LocalDate.of(2025, 5, 18))
                .status(DoctorScheduleDate.Status.INACTIVE)
                .notes("Afternoon")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(exampleScheduleDate));
        when(repository.save(any(DoctorScheduleDate.class))).thenAnswer(i -> i.getArgument(0));

        DoctorScheduleDate result = service.update(1L, updated);

        assertEquals(updated.getDate(), result.getDate());
        assertEquals(updated.getStatus(), result.getStatus());
        assertEquals(updated.getNotes(), result.getNotes());
    }

    @Test
    void testUpdateWhenNotExists() {
        DoctorScheduleDate updated = DoctorScheduleDate.builder().build();

        when(repository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.update(1L, updated));
        assertTrue(ex.getMessage().contains("DoctorScheduleDate no encontrada"));
    }

    @Test
    void testDeleteWhenExists() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.delete(1L));
        verify(repository).deleteById(1L);
    }

    @Test
    void testDeleteWhenNotExists() {
        when(repository.existsById(1L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.delete(1L));
        assertTrue(ex.getMessage().contains("DoctorScheduleDate no encontrada"));
    }
}

