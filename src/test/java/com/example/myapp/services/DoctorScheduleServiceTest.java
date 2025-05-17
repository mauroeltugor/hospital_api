package com.example.myapp.services;

import com.example.myapp.entity.Doctor;
import com.example.myapp.entity.DoctorSchedule;
import com.example.myapp.repository.DoctorRepository;
import com.example.myapp.repository.DoctorScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DoctorScheduleServiceTest {

    @Mock
    private DoctorScheduleRepository scheduleRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorScheduleService scheduleService;

    private Doctor doctor;
    private DoctorSchedule schedule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        doctor = Doctor.builder()
                .id(1L)
                .build();

        schedule = DoctorSchedule.builder()
                .id(1L)
                .doctor(doctor)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .maxAppointments(10)
                .workDay(DoctorSchedule.WorkDay.MORNING)
                .build();
    }

    @Test
    void testCreateSchedule_Success() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(scheduleRepository.save(any())).thenReturn(schedule);

        DoctorSchedule result = scheduleService.createSchedule(1L, schedule);

        assertNotNull(result);
        assertEquals(doctor, result.getDoctor());
        verify(scheduleRepository).save(schedule);
    }

    @Test
    void testCreateSchedule_DoctorNotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            scheduleService.createSchedule(1L, schedule);
        });

        assertTrue(exception.getMessage().contains("Doctor no encontrado"));
    }

    @Test
    void testUpdateSchedule_Success() {
        DoctorSchedule updated = DoctorSchedule.builder()
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
                .maxAppointments(15)
                .workDay(DoctorSchedule.WorkDay.NIGHT)
                .build();

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        DoctorSchedule result = scheduleService.updateSchedule(1L, updated);

        assertEquals(LocalTime.of(10, 0), result.getStartTime());
        assertEquals(DoctorSchedule.WorkDay.NIGHT, result.getWorkDay());
        assertEquals(15, result.getMaxAppointments());
        verify(scheduleRepository).save(schedule);
    }

    @Test
    void testUpdateSchedule_NotFound() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            scheduleService.updateSchedule(1L, schedule);
        });

        assertTrue(exception.getMessage().contains("Horario no encontrado"));
    }

    @Test
    void testDeleteSchedule_Success() {
        when(scheduleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(scheduleRepository).deleteById(1L);

        assertDoesNotThrow(() -> scheduleService.deleteSchedule(1L));
        verify(scheduleRepository).deleteById(1L);
    }

    @Test
    void testDeleteSchedule_NotFound() {
        when(scheduleRepository.existsById(1L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            scheduleService.deleteSchedule(1L);
        });

        assertTrue(exception.getMessage().contains("Horario no encontrado"));
    }

    @Test
    void testGetSchedule_Success() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        DoctorSchedule result = scheduleService.getSchedule(1L);

        assertEquals(schedule, result);
    }

    @Test
    void testGetSchedule_NotFound() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            scheduleService.getSchedule(1L);
        });

        assertTrue(exception.getMessage().contains("Horario no encontrado"));
    }

    @Test
    void testGetAllSchedules() {
        when(scheduleRepository.findAll()).thenReturn(List.of(schedule));

        List<DoctorSchedule> result = scheduleService.getAllSchedules();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}

