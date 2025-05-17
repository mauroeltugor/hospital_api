package com.example.myapp.services;


import com.example.myapp.entity.Doctor;
import com.example.myapp.entity.DoctorSchedule;

import com.example.myapp.repository.DoctorRepository;
import com.example.myapp.repository.DoctorScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService {

    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;

    public DoctorSchedule createSchedule(Long doctorId, DoctorSchedule schedule) {
        // Obtener doctor usando repo, necesario para relación
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor no encontrado con ID: " + doctorId));
        schedule.setDoctor(doctor);
        return scheduleRepository.save(schedule);
    }

    public DoctorSchedule updateSchedule(Long id, DoctorSchedule updated) {
        // Buscar horario, modificar campos y guardar
        DoctorSchedule existing = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horario no encontrado con id: " + id));
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setBreakStart(updated.getBreakStart());
        existing.setBreakEnd(updated.getBreakEnd());
        existing.setMaxAppointments(updated.getMaxAppointments());
        existing.setWorkDay(updated.getWorkDay());

        return scheduleRepository.save(existing);
    }

    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new EntityNotFoundException("Horario no encontrado con id: " + id);
        }
        scheduleRepository.deleteById(id);
    }

    public DoctorSchedule getSchedule(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horario no encontrado con id: " + id));
    }

    public List<DoctorSchedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }
}
