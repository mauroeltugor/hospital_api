package com.example.hospital_api.repository;

import com.example.hospital_api.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Date;
import java.util.List;
@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    Session findByAppointmentId(int appointmentId);
    List<Session> findBySessionDateBetween(Date startDate, Date endDate);
    List<Session> findByEffectivenessGreaterThanEqual(int effectiveness);
}