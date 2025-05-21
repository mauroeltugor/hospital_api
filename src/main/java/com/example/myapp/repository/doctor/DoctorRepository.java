package com.example.myapp.repository.doctor;

import com.example.myapp.entity.Doctor;
import com.example.myapp.entity.DoctorSchedule;
import com.example.myapp.entity.DoctorSpecialty.ExperienceLevel;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Doctor entities.
 * Provides comprehensive queries for medical system requirements.
 */
@Repository
public interface DoctorRepository extends BaseRepository<Doctor, Long> {
    
    /**
     * Find a doctor by their license number.
     */
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
    
    /**
     * Find doctors by their specialty name.
     */
    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.specialties ds JOIN ds.specialty s " +
           "WHERE LOWER(s.name) = LOWER(:specialtyName)")
    List<Doctor> findBySpecialtyName(@Param("specialtyName") String specialtyName);
    
    /**
     * Find doctors with a specific specialty and experience level.
     */
    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.specialties ds JOIN ds.specialty s " +
           "WHERE s.id = :specialtyId AND ds.experienceLevel = :level")
    List<Doctor> findBySpecialtyIdAndExperienceLevel(
            @Param("specialtyId") Long specialtyId,
            @Param("level") ExperienceLevel level);
    
    /**
     * Find doctors available for appointment on a specific date.
     * Checks schedules, dates, and existing appointments.
     */
    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.schedules s JOIN s.scheduleDates sd " +
           "WHERE sd.date = :date AND sd.status = 'ACTIVE' " +
           "AND NOT EXISTS (SELECT a FROM Appointment a WHERE a.schedule = s " +
           "AND a.status IN ('SCHEDULED', 'CONFIRMED'))")
    List<Doctor> findAvailableDoctorsByDate(@Param("date") LocalDate date);
    
    /**
     * Find doctors available during a specific time slot.
     */
    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.schedules s " +
           "WHERE s.startTime <= :endTime AND s.endTime >= :startTime " +
           "AND s.workDay = :workDay")
    List<Doctor> findDoctorsByTimeSlot(
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("workDay") DoctorSchedule.WorkDay workDay);
    
    /**
     * Find doctors who can treat a specific diagnosis.
     */
    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.specialties ds JOIN ds.specialty s " +
           "JOIN s.diagnoses diag WHERE diag.id = :diagnosisId")
    List<Doctor> findByDiagnosisId(@Param("diagnosisId") Long diagnosisId);
    
    /**
     * Find doctors who treated a specific patient.
     */
    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.medicalRecordItems mri " +
           "JOIN mri.medicalRecord mr WHERE mr.patient.id = :patientId")
    List<Doctor> findByPatientId(@Param("patientId") Long patientId);
    
    /**
 * Find doctors ordered by appointment count (busiest doctors).
 */
@Query("SELECT d, COUNT(a) as appointmentCount FROM Doctor d " +
       "JOIN d.schedules s JOIN s.appointments a " +
       "WHERE a.createdAt BETWEEN :startDate AND :endDate " +
       "GROUP BY d ORDER BY appointmentCount DESC")
List<Object[]> findDoctorsByAppointmentCount(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable);
    
    /**
     * Find doctors with certification date in a specific range.
     */
    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.specialties ds " +
           "WHERE ds.certificationDate BETWEEN :startDate AND :endDate")
    List<Doctor> findByCertificationDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * Find doctors with most prescriptions in a time period.
     */
    @Query("SELECT d, COUNT(p) as prescriptionCount FROM Doctor d " +
           "JOIN d.prescriptions p " +
           "WHERE p.issuedAt BETWEEN :startDate AND :endDate " +
           "GROUP BY d ORDER BY prescriptionCount DESC")
    List<Object[]> findTopPrescribingDoctors(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    /**
     * Find doctors who can perform a specific treatment.
     */
    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.specialties ds JOIN ds.specialty s " +
           "JOIN s.treatments t WHERE t.id = :treatmentId")
    List<Doctor> findByTreatmentId(@Param("treatmentId") Long treatmentId);
    
    /**
     * Find doctors with a minimum number of specialties.
     */
    @Query("SELECT d FROM Doctor d JOIN d.specialties ds " +
           "GROUP BY d HAVING COUNT(ds) >= :minSpecialties")
    List<Doctor> findByMinimumSpecialtiesCount(@Param("minSpecialties") Long minSpecialties);
    
    /**
     * Search doctors by name, specialty, or license number.
     */
    @Query("SELECT DISTINCT d FROM Doctor d LEFT JOIN d.specialties ds LEFT JOIN ds.specialty s " +
           "WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(d.licenseNumber) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(s.name) LIKE LOWER(CONCAT('%', :term, '%'))")
    Page<Doctor> searchDoctors(@Param("term") String searchTerm, Pageable pageable);
    
    /**
     * Find doctors with no appointments in a time period (possibly inactive).
     */
    @Query("SELECT d FROM Doctor d WHERE NOT EXISTS " +
           "(SELECT a FROM Appointment a JOIN a.schedule s WHERE s.doctor = d " +
           "AND a.createdAt > :sinceDate)")
    List<Doctor> findInactiveDoctors(@Param("sinceDate") LocalDateTime sinceDate);
    
    /**
    * Find doctors by their effectiveness rating (average of appointments effectiveness).
    */
    @Query("SELECT d, AVG(a.effectiveness) as avgEffectiveness FROM Doctor d " +
        "JOIN d.schedules s JOIN s.appointments a " +
       "WHERE a.effectiveness IS NOT NULL " +
       "GROUP BY d HAVING AVG(a.effectiveness) >= :minEffectiveness " +
       "ORDER BY avgEffectiveness DESC")
    List<Object[]> findByMinimumEffectiveness(
            @Param("minEffectiveness") Double minEffectiveness,
            Pageable pageable);
}