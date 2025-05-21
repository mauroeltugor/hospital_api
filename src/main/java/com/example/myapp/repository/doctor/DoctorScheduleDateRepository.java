package com.example.myapp.repository.doctor;

import com.example.myapp.entity.DoctorScheduleDate;
import com.example.myapp.entity.DoctorScheduleDate.Status;
import com.example.myapp.repository.base.BaseRepository;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing doctor schedule dates.
 * Provides queries for tracking and analyzing doctor availability.
 */
@Repository
public interface DoctorScheduleDateRepository extends BaseRepository<DoctorScheduleDate, Long> {

    /**
     * Find a schedule date by schedule ID and date.
     *
     * @param scheduleId The schedule ID
     * @param date The specific date
     * @return The schedule date if found
     */
    Optional<DoctorScheduleDate> findByScheduleIdAndDate(Long scheduleId, LocalDate date);
    
    /**
     * Find schedule dates by schedule ID.
     *
     * @param scheduleId The schedule ID
     * @return List of schedule dates for the schedule
     */
    List<DoctorScheduleDate> findByScheduleId(Long scheduleId);
    
    /**
     * Find schedule dates by status.
     *
     * @param status The status (ACTIVE or INACTIVE)
     * @return List of schedule dates with the specified status
     */
    List<DoctorScheduleDate> findByStatus(Status status);
    
    /**
     * Find schedule dates for a specific date.
     *
     * @param date The date to check
     * @return List of schedule dates on the specified date
     */
    List<DoctorScheduleDate> findByDate(LocalDate date);
    
    /**
     * Find schedule dates in a date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of schedule dates in the date range
     */
    List<DoctorScheduleDate> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find schedule dates for a specific doctor.
     *
     * @param doctorId The doctor's ID
     * @return List of schedule dates for the doctor
     */
    @Query("SELECT sd FROM DoctorScheduleDate sd JOIN sd.schedule s " +
           "WHERE s.doctor.id = :doctorId")
    List<DoctorScheduleDate> findByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Find schedule dates for a specific doctor on a specific date.
     *
     * @param doctorId The doctor's ID
     * @param date The specific date
     * @return List of schedule dates for the doctor on the date
     */
    @Query("SELECT sd FROM DoctorScheduleDate sd JOIN sd.schedule s " +
           "WHERE s.doctor.id = :doctorId AND sd.date = :date")
    List<DoctorScheduleDate> findByDoctorIdAndDate(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date);
    
    /**
     * Find active schedule dates for a specific doctor in a date range.
     *
     * @param doctorId The doctor's ID
     * @param startDate The start date
     * @param endDate The end date
     * @return List of active schedule dates for the doctor in the date range
     */
    @Query("SELECT sd FROM DoctorScheduleDate sd JOIN sd.schedule s " +
           "WHERE s.doctor.id = :doctorId AND sd.date BETWEEN :startDate AND :endDate " +
           "AND sd.status = 'ACTIVE' ORDER BY sd.date")
    List<DoctorScheduleDate> findActiveDatesByDoctorIdAndDateRange(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * Find schedule dates with available appointment slots.
     * This checks if the number of appointments is less than the maximum allowed.
     *
     * @param date The date to check
     * @return List of schedule dates with available slots
     */
    @Query("SELECT sd FROM DoctorScheduleDate sd JOIN sd.schedule s " +
           "LEFT JOIN s.appointments a ON sd.date = :date " +
           "WHERE sd.date = :date AND sd.status = 'ACTIVE' " +
           "GROUP BY sd, s HAVING COUNT(a) < s.maxAppointments")
    List<DoctorScheduleDate> findAvailableDatesByDate(@Param("date") LocalDate date);
    
    /**
     * Find schedule dates for doctors with a specific specialty.
     *
     * @param specialtyId The specialty ID
     * @param date The date to check
     * @return List of schedule dates for doctors with the specialty on the date
     */
    @Query("SELECT sd FROM DoctorScheduleDate sd JOIN sd.schedule s JOIN s.doctor d " +
           "JOIN d.specialties ds WHERE ds.specialty.id = :specialtyId " +
           "AND sd.date = :date AND sd.status = 'ACTIVE'")
    List<DoctorScheduleDate> findBySpecialtyIdAndDate(
            @Param("specialtyId") Long specialtyId,
            @Param("date") LocalDate date);
    
    /**
     * Count active dates by doctor.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Active date counts grouped by doctor
     */
    @Query("SELECT s.doctor.id as doctorId, s.doctor.firstName as firstName, " +
           "s.doctor.lastName as lastName, COUNT(sd) as count " +
           "FROM DoctorScheduleDate sd JOIN sd.schedule s " +
           "WHERE sd.date BETWEEN :startDate AND :endDate AND sd.status = 'ACTIVE' " +
           "GROUP BY s.doctor.id, s.doctor.firstName, s.doctor.lastName " +
           "ORDER BY count DESC")
    List<Object[]> countActiveDatesByDoctor(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * Count schedule dates by status.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Date counts grouped by status
     */
    @Query("SELECT sd.status as status, COUNT(sd) as count " +
           "FROM DoctorScheduleDate sd " +
           "WHERE sd.date BETWEEN :startDate AND :endDate " +
           "GROUP BY sd.status")
    List<Object[]> countDatesByStatus(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * Find doctors with the most active dates.
     * Useful for identifying most available doctors.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @param limit The maximum number of results
     * @return Doctor availability statistics
     */
    @Query("SELECT s.doctor.id as doctorId, s.doctor.firstName as firstName, " +
           "s.doctor.lastName as lastName, COUNT(sd) as count " +
           "FROM DoctorScheduleDate sd JOIN sd.schedule s " +
           "WHERE sd.date BETWEEN :startDate AND :endDate AND sd.status = 'ACTIVE' " +
           "GROUP BY s.doctor.id, s.doctor.firstName, s.doctor.lastName " +
           "ORDER BY count DESC")
    List<Object[]> findDoctorsWithMostActiveDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
    
    /**
     * Find schedule dates with notes containing specific text.
     *
     * @param searchText The text to search for
     * @return List of schedule dates with matching notes
     */
    @Query("SELECT sd FROM DoctorScheduleDate sd WHERE LOWER(sd.notes) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<DoctorScheduleDate> findByNotesContainingIgnoreCase(@Param("searchText") String searchText);
    
    /**
     * Find schedule dates that have changes in status.
     * This finds dates where the status has been updated after creation.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of schedule dates with status changes
     */
    @Query("SELECT sd FROM DoctorScheduleDate sd " +
           "WHERE sd.date BETWEEN :startDate AND :endDate " +
           "AND sd.updatedAt > sd.createdAt")
    List<DoctorScheduleDate> findDatesWithStatusChanges(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * Find schedule dates by day of week.
     * Useful for analyzing doctor availability patterns.
     *
     * @param dayOfWeek The day of week (1=Monday, 7=Sunday)
     * @param startDate The start date
     * @param endDate The end date
     * @return List of schedule dates on the specified day of week
     */
    @Query("SELECT sd FROM DoctorScheduleDate sd " +
           "WHERE sd.date BETWEEN :startDate AND :endDate " +
           "AND FUNCTION('DAYOFWEEK', sd.date) = :dayOfWeek")
    List<DoctorScheduleDate> findByDayOfWeek(
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * Find recently added schedule dates.
     *
     * @param days The number of days to look back
     * @return List of recently added schedule dates
     */
    @Query("SELECT sd FROM DoctorScheduleDate sd " +
           "WHERE sd.createdAt >= FUNCTION('DATE_SUB', CURRENT_TIMESTAMP, :days) " +
           "ORDER BY sd.createdAt DESC")
    List<DoctorScheduleDate> findRecentlyAddedDates(@Param("days") Integer days);
    
    /**
     * Check if a doctor is available on a specific date.
     *
     * @param doctorId The doctor's ID
     * @param date The date to check
     * @return True if the doctor is available, false otherwise
     */
    @Query("SELECT COUNT(sd) > 0 FROM DoctorScheduleDate sd JOIN sd.schedule s " +
           "WHERE s.doctor.id = :doctorId AND sd.date = :date AND sd.status = 'ACTIVE'")
    boolean isDoctorAvailableOnDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);
}