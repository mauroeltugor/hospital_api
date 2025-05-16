package com.example.myapp.services;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.myapp.dto.AppointmentDTO;
import com.example.myapp.entity.Appointment;
import com.example.myapp.entity.Appointment.Status;
import com.example.myapp.repository.AppointmentRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public void cancelAppointment(Integer id, AppointmentDTO request) throws Exception {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            appointment.setStatus(Status.CANCELLED);
            appointment.setUpdatedAt(LocalDateTime.now());
            appointmentRepository.save(appointment);
            // Aquí se puede implementar el envío de notificaciones y registro histórico
        } else {
            throw new Exception("Cita no encontrada");
        }
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date, Long doctorId, Long specialtyId) {
        return appointmentRepository.findByDateAndFilters(date, doctorId, specialtyId);
    }

    public List<Appointment> getAppointmentStatistics(LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.getAppointmentStatistics(startDate, endDate);
    }
}
