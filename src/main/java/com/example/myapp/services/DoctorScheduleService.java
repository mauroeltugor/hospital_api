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
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor no encontrado con ID: " + doctorId));
        schedule.setDoctor(doctor);
        return scheduleRepository.save(schedule);
    }

    public DoctorSchedule updateSchedule(Long id, DoctorSchedule updated) {
        return scheduleRepository.findById(id).map(schedule -> {
            schedule.setStartTime(updated.getStartTime());
            schedule.setEndTime(updated.getEndTime());
            schedule.setBreakStart(updated.getBreakStart());
            schedule.setBreakEnd(updated.getBreakEnd());
            schedule.setMaxAppointments(updated.getMaxAppointments());
            schedule.setWorkDay(updated.getWorkDay());
            
            return scheduleRepository.save(schedule);
        }).orElseThrow(() -> new EntityNotFoundException("Horario no encontrado"));
    }

    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new EntityNotFoundException("Horario no encontrado");
        }
        scheduleRepository.deleteById(id);
    }

    public DoctorSchedule getSchedule(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Horario no encontrado"));
    }

    public List<DoctorSchedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }
}
