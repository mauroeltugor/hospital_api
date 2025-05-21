package com.example.myapp.repository.doctor;

import com.example.myapp.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    
    // Encontrar horarios por doctor
    List<DoctorSchedule> findByDoctorId(Long doctorId);
    
    // Encontrar por doctor y día de trabajo
    Optional<DoctorSchedule> findByDoctorIdAndWorkDay(Long doctorId, DoctorSchedule.WorkDay workDay);
    
    // Buscar horarios con slots disponibles para una fecha
    @Query("SELECT ds FROM DoctorSchedule ds JOIN ds.scheduleDates sd WHERE sd.date = :date AND (SELECT COUNT(a) FROM Appointment a WHERE a.schedule.id = ds.id AND a.status != 'CANCELLED') < ds.maxAppointments")
    List<DoctorSchedule> findWithAvailableSlotsOnDate(@Param("date") LocalDate date);
    
    // Buscar horarios por doctores específicos y fecha
    @Query("SELECT ds FROM DoctorSchedule ds JOIN ds.scheduleDates sd WHERE ds.doctor.id IN :doctorIds AND sd.date = :date AND sd.status = 'ACTIVE'")
    List<DoctorSchedule> findByDoctorIdsAndDate(@Param("doctorIds") List<Long> doctorIds, @Param("date") LocalDate date);
    
    // Contar citas para un horario específico
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.schedule.id = :scheduleId AND a.status != 'CANCELLED'")
    int countAppointmentsByScheduleAndDate(@Param("scheduleId") Long scheduleId);
    
    // Verificar disponibilidad de horario
    @Query("SELECT CASE WHEN COUNT(a) < ds.maxAppointments THEN true ELSE false END FROM DoctorSchedule ds LEFT JOIN ds.appointments a " +
           "WHERE ds.id = :scheduleId AND a.status != 'CANCELLED' " +
           "GROUP BY ds.maxAppointments")
    boolean isTimeSlotAvailable(@Param("scheduleId") Long scheduleId);
    
    // Buscar horarios activos por especialidad
    @Query("SELECT ds FROM DoctorSchedule ds WHERE ds.doctor.id IN " +
           "(SELECT ds2.doctor.id FROM DoctorSpecialty ds2 WHERE ds2.specialty.id = :specialtyId) " +
           "AND EXISTS (SELECT sd FROM ds.scheduleDates sd WHERE sd.date >= :startDate AND sd.date <= :endDate AND sd.status = 'ACTIVE')")
    List<DoctorSchedule> findActiveSchedulesBySpecialtyAndDateRange(@Param("specialtyId") Long specialtyId,
                                                                   @Param("startDate") LocalDate startDate,
                                                                   @Param("endDate") LocalDate endDate);
    
    // Buscar horarios con alta disponibilidad
    @Query("SELECT ds FROM DoctorSchedule ds WHERE ds.maxAppointments - " +
           "(SELECT COUNT(a) FROM Appointment a WHERE a.schedule.id = ds.id AND a.status != 'CANCELLED') >= :minAvailable " +
           "AND EXISTS (SELECT sd FROM ds.scheduleDates sd WHERE sd.date = :date AND sd.status = 'ACTIVE')")
    List<DoctorSchedule> findSchedulesWithHighAvailability(@Param("date") LocalDate date, @Param("minAvailable") int minAvailable);
}