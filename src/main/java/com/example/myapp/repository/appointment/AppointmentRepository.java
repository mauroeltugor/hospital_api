package com.example.myapp.repository.appointment;

import com.example.myapp.entity.Appointment;
import com.example.myapp.entity.Appointment.Status;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing medical appointments.
 * Provides comprehensive queries for appointment scheduling, tracking and reporting.
 */
@Repository
public interface AppointmentRepository extends BaseRepository<Appointment, Long> {

    /**
     * Find an appointment by ID with eager loading of related entities.
     *
     * @param id The appointment ID
     * @return The appointment with loaded relationships if found
     */
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.schedule s " +
           "LEFT JOIN FETCH s.doctor d " +
           "LEFT JOIN FETCH a.patient p " +
           "LEFT JOIN FETCH a.specialty sp " +
           "WHERE a.id = :id")
    Optional<Appointment> findByIdWithDetails(@Param("id") Long id);
    
    /**
     * Find all appointments for a specific patient.
     *
     * @param patientId The patient's ID
     * @return List of appointments for the patient
     */
    List<Appointment> findByPatientId(Long patientId);
    
    /**
     * Find all appointments in a specific schedule.
     *
     * @param scheduleId The schedule's ID
     * @return List of appointments in the schedule
     */
    List<Appointment> findByScheduleId(Long scheduleId);
    
    /**
     * Find all appointments for a doctor through the schedule relationship.
     *
     * @param doctorId The doctor's ID
     * @return List of appointments for the doctor
     */
    @Query("SELECT a FROM Appointment a JOIN a.schedule s WHERE s.doctor.id = :doctorId")
    List<Appointment> findByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Find appointments for a specific date.
     *
     * @param date The date to search for
     * @return List of appointments on the date
     */
    @Query("SELECT a FROM Appointment a JOIN a.schedule s JOIN s.scheduleDates sd " +
           "WHERE sd.date = :date")
    List<Appointment> findByDate(@Param("date") LocalDate date);
    
    /**
     * Find appointments for a doctor on a specific date.
     *
     * @param doctorId The doctor's ID
     * @param date The date to search for
     * @return List of appointments for the doctor on the date
     */
    @Query("SELECT a FROM Appointment a JOIN a.schedule s JOIN s.scheduleDates sd " +
           "WHERE s.doctor.id = :doctorId AND sd.date = :date")
    List<Appointment> findByDoctorIdAndDate(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date);
    
    /**
     * Find appointments for a patient in a date range.
     *
     * @param patientId The patient's ID
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return List of appointments for the patient in the date range
     */
    @Query("SELECT a FROM Appointment a JOIN a.schedule s JOIN s.scheduleDates sd " +
           "WHERE a.patient.id = :patientId AND sd.date BETWEEN :startDate AND :endDate")
    List<Appointment> findByPatientIdAndDateRange(
            @Param("patientId") Long patientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * Find appointments by status.
     *
     * @param status The appointment status
     * @return List of appointments with the specified status
     */
    List<Appointment> findByStatus(Status status);
    
    /**
     * Find appointments with a specific specialty.
     *
     * @param specialtyId The specialty ID
     * @return List of appointments for the specialty
     */
    List<Appointment> findBySpecialtyId(Long specialtyId);
    
    /**
     * Find appointments that need confirmation (scheduled but not confirmed).
     *
     * @param currentDate The current date for comparison
     * @return List of appointments needing confirmation
     */
    @Query("SELECT a FROM Appointment a JOIN a.schedule s JOIN s.scheduleDates sd " +
           "WHERE a.status = 'SCHEDULED' AND sd.date >= :currentDate")
    List<Appointment> findAppointmentsNeedingConfirmation(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find appointments for a date range with pagination.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @param pageable Pagination information
     * @return Page of appointments in the date range
     */
    @Query("SELECT a FROM Appointment a JOIN a.schedule s JOIN s.scheduleDates sd " +
           "WHERE sd.date BETWEEN :startDate AND :endDate")
    Page<Appointment> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
    
    /**
     * Count appointments by status for a specific date range.
     * Useful for dashboard statistics.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Appointment counts grouped by status
     */
    @Query("SELECT a.status as status, COUNT(a) as count FROM Appointment a " +
           "JOIN a.schedule s JOIN s.scheduleDates sd " +
           "WHERE sd.date BETWEEN :startDate AND :endDate " +
           "GROUP BY a.status")
    List<Object[]> countAppointmentsByStatus(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * Find appointments with high effectiveness rating.
     *
     * @param minEffectiveness The minimum effectiveness value
     * @return List of highly effective appointments
     */
    List<Appointment> findByEffectivenessGreaterThanEqual(Integer minEffectiveness);
    
    /**
     * Find appointments that have associated prescriptions.
     *
     * @return List of appointments with prescriptions
     */
    @Query("SELECT DISTINCT a FROM Appointment a JOIN a.prescriptions p")
    List<Appointment> findAppointmentsWithPrescriptions();
    
    /**
     * Find upcoming appointments for a patient.
     *
     * @param patientId The patient's ID
     * @param currentDate The current date
     * @return List of upcoming appointments
     */
    @Query("SELECT a FROM Appointment a JOIN a.schedule s JOIN s.scheduleDates sd " +
           "WHERE a.patient.id = :patientId AND sd.date >= :currentDate " +
           "AND a.status IN ('SCHEDULED', 'CONFIRMED') " +
           "ORDER BY sd.date ASC")
    List<Appointment> findUpcomingAppointmentsForPatient(
            @Param("patientId") Long patientId,
            @Param("currentDate") LocalDate currentDate);
    
    /**
     * Find no-show appointments for a patient.
     *
     * @param patientId The patient's ID
     * @return List of no-show appointments
     */
    List<Appointment> findByPatientIdAndStatus(Long patientId, Status status);
    
    /**
     * Find appointments by specialty and date range.
     *
     * @param specialtyId The specialty ID
     * @param startDate The start date
     * @param endDate The end date
     * @return List of appointments for the specialty in the date range
     */
    @Query("SELECT a FROM Appointment a JOIN a.schedule s JOIN s.scheduleDates sd " +
           "WHERE a.specialty.id = :specialtyId AND sd.date BETWEEN :startDate AND :endDate")
    List<Appointment> findBySpecialtyIdAndDateRange(
            @Param("specialtyId") Long specialtyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * Count appointments by specialty.
     * Useful for analytics on department workload.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Appointment counts grouped by specialty
     */
    @Query("SELECT a.specialty.id as specialtyId, a.specialty.name as specialtyName, " +
           "COUNT(a) as count FROM Appointment a " +
           "JOIN a.schedule s JOIN s.scheduleDates sd " +
           "WHERE sd.date BETWEEN :startDate AND :endDate " +
           "GROUP BY a.specialty.id, a.specialty.name")
    List<Object[]> countAppointmentsBySpecialty(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}