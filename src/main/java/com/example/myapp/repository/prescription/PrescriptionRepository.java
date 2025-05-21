package com.example.myapp.repository.prescription;

import com.example.myapp.entity.Prescription;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing medical prescriptions.
 * Provides queries for prescription tracking, analysis and reporting.
 */
@Repository
public interface PrescriptionRepository extends BaseRepository<Prescription, Long> {

    /**
     * Find a prescription by ID with all related details.
     *
     * @param id The prescription ID
     * @return The prescription with loaded relationships if found
     */
    @Query("SELECT p FROM Prescription p " +
           "LEFT JOIN FETCH p.appointment a " +
           "LEFT JOIN FETCH p.patient pt " +
           "LEFT JOIN FETCH p.doctor d " +
           "WHERE p.id = :id")
    Optional<Prescription> findByIdWithDetails(@Param("id") Long id);
    
    /**
     * Find all prescriptions for a specific patient.
     *
     * @param patientId The patient's ID
     * @return List of prescriptions for the patient
     */
    List<Prescription> findByPatientId(Long patientId);
    
    /**
     * Find all prescriptions issued by a specific doctor.
     *
     * @param doctorId The doctor's ID
     * @return List of prescriptions issued by the doctor
     */
    List<Prescription> findByDoctorId(Long doctorId);
    
    /**
     * Find all prescriptions associated with a specific appointment.
     *
     * @param appointmentId The appointment's ID
     * @return List of prescriptions for the appointment
     */
    List<Prescription> findByAppointmentId(Long appointmentId);
    
    /**
     * Find active prescriptions (not expired).
     *
     * @param currentDateTime The current date and time
     * @return List of active prescriptions
     */
    @Query("SELECT p FROM Prescription p WHERE " +
           "(p.expiresAt IS NULL OR p.expiresAt > :currentDateTime)")
    List<Prescription> findActivePrescriptions(@Param("currentDateTime") LocalDateTime currentDateTime);
    
    /**
     * Find expired prescriptions.
     *
     * @param currentDateTime The current date and time
     * @return List of expired prescriptions
     */
    @Query("SELECT p FROM Prescription p WHERE " +
           "p.expiresAt IS NOT NULL AND p.expiresAt <= :currentDateTime")
    List<Prescription> findExpiredPrescriptions(@Param("currentDateTime") LocalDateTime currentDateTime);
    
    /**
     * Find prescriptions issued in a specific date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of prescriptions issued in the date range
     */
    @Query("SELECT p FROM Prescription p WHERE " +
           "p.issuedAt BETWEEN :startDate AND :endDate")
    List<Prescription> findByIssuedDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find prescriptions for a patient that include a specific treatment.
     *
     * @param patientId The patient's ID
     * @param treatmentId The treatment's ID
     * @return List of prescriptions for the patient with the treatment
     */
    @Query("SELECT p FROM Prescription p JOIN p.treatments pt " +
           "WHERE p.patient.id = :patientId AND pt.treatment.id = :treatmentId")
    List<Prescription> findByPatientIdAndTreatmentId(
            @Param("patientId") Long patientId,
            @Param("treatmentId") Long treatmentId);
    
    /**
     * Find prescriptions by specialty.
     *
     * @param specialtyId The specialty's ID
     * @return List of prescriptions for the specialty
     */
    @Query("SELECT p FROM Prescription p JOIN p.specialties ps " +
           "WHERE ps.specialty.id = :specialtyId")
    List<Prescription> findBySpecialtyId(@Param("specialtyId") Long specialtyId);
    
    /**
     * Find prescriptions associated with a specific diagnosis through medical record items.
     *
     * @param diagnosisId The diagnosis ID
     * @return List of prescriptions for the diagnosis
     */
    @Query("SELECT DISTINCT p FROM Prescription p JOIN p.medicalRecordItems mrip " +
           "JOIN mrip.medicalRecordItem mri JOIN mri.diagnoses dmi " +
           "WHERE dmi.diagnosis.id = :diagnosisId")
    List<Prescription> findByDiagnosisId(@Param("diagnosisId") Long diagnosisId);
    
    /**
     * Find prescriptions with notes containing specific keywords.
     *
     * @param keyword The keyword to search for
     * @return List of prescriptions with matching notes
     */
    @Query("SELECT p FROM Prescription p WHERE LOWER(p.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Prescription> findByNotesContainingIgnoreCase(@Param("keyword") String keyword);
    
    /**
     * Count prescriptions by doctor in a date range.
     * Useful for analytics on prescribing patterns.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Prescription counts by doctor
     */
    @Query("SELECT p.doctor.id as doctorId, p.doctor.firstName as firstName, " +
           "p.doctor.lastName as lastName, COUNT(p) as count FROM Prescription p " +
           "WHERE p.issuedAt BETWEEN :startDate AND :endDate " +
           "GROUP BY p.doctor.id, p.doctor.firstName, p.doctor.lastName " +
           "ORDER BY count DESC")
    List<Object[]> countPrescriptionsByDoctor(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find prescriptions that expire soon.
     *
     * @param currentDate The current date
     * @param daysThreshold The days threshold
     * @return List of prescriptions expiring soon
     */
    @Query("SELECT p FROM Prescription p WHERE p.expiresAt IS NOT NULL " +
           "AND p.expiresAt BETWEEN :currentDate AND :currentDate + :daysThreshold")
    List<Prescription> findPrescriptionsExpiringSoon(
            @Param("currentDate") LocalDateTime currentDate,
            @Param("daysThreshold") java.time.temporal.TemporalAmount daysThreshold);
    
    /**
     * Find most recent prescriptions for a patient with pagination.
     *
     * @param patientId The patient's ID
     * @param pageable Pagination information
     * @return Page of prescriptions for the patient
     */
    @Query("SELECT p FROM Prescription p WHERE p.patient.id = :patientId " +
           "ORDER BY p.issuedAt DESC")
    Page<Prescription> findRecentPrescriptionsForPatient(
            @Param("patientId") Long patientId,
            Pageable pageable);
    
    /**
     * Find prescriptions for a patient by date range.
     *
     * @param patientId The patient's ID
     * @param startDate The start date
     * @param endDate The end date
     * @return List of prescriptions for the patient in the date range
     */
    @Query("SELECT p FROM Prescription p WHERE p.patient.id = :patientId " +
           "AND p.issuedAt BETWEEN :startDate AND :endDate")
    List<Prescription> findByPatientIdAndDateRange(
            @Param("patientId") Long patientId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count prescriptions by treatment type.
     * Useful for analytics on common treatments.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Treatment counts grouped by treatment
     */
    @Query("SELECT pt.treatment.id as treatmentId, pt.treatment.name as treatmentName, " +
           "COUNT(pt) as count FROM Prescription p JOIN p.treatments pt " +
           "WHERE p.issuedAt BETWEEN :startDate AND :endDate " +
           "GROUP BY pt.treatment.id, pt.treatment.name " +
           "ORDER BY count DESC")
    List<Object[]> countPrescriptionsByTreatment(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}