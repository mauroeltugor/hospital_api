package com.example.myapp.repository;

import com.example.myapp.entity.Appointment;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

     @Query("SELECT a FROM Appointment a WHERE CAST(a.createdAt AS date) = :date AND (:doctorId IS NULL OR a.doctorId = :doctorId) AND (:specialtyId IS NULL OR a.specialtyId = :specialtyId)")
    List<Appointment> findByDateAndFilters(@Param("date") LocalDate date, @Param("doctorId") Long doctorId, @Param("specialtyId") Long specialtyId);

    @Query("SELECT new com.hospital.api.AppointmentStatistics(a.status, COUNT(a)) FROM Appointment a WHERE a.createdAt BETWEEN :startDate AND :endDate GROUP BY a.status")
    List<Appointment> getAppointmentStatistics(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
