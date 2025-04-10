package com.example.hospital_api.repository;

import com.example.hospital_api.entity.*;
import com.example.hospital_api.entity.Appointment.AppointmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByDoctorId(int doctorId);
    List<Appointment> findByPatientId(int patientId);
    List<Appointment> findByScheduleDateId(int scheduleDateId);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}