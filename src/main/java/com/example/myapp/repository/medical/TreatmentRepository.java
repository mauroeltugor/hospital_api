package com.example.myapp.repository.medical;

import com.example.myapp.entity.Treatment;
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
 * Repository for managing medical treatments.
 * Provides queries for treatment-related operations and analytics.
 */
@Repository
public interface TreatmentRepository extends BaseRepository<Treatment, Long> {

    /**
     * Find a treatment by name (case insensitive).
     *
     * @param name The treatment name
     * @return The treatment if found
     */
    Optional<Treatment> findByNameIgnoreCase(String name);
    
    /**
     * Find treatments by specialty ID.
     *
     * @param specialtyId The specialty ID
     * @return List of treatments in the specialty
     */
    List<Treatment> findBySpecialtyId(Long specialtyId);
    
    /**
     * Find treatments that contain specific text in name or description.
     *
     * @param searchText The text to search for
     * @return List of matching treatments
     */
    @Query("SELECT t FROM Treatment t WHERE " +
           "LOWER(t.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Treatment> findByNameOrDescriptionContainingIgnoreCase(@Param("searchText") String searchText);
    
    /**
     * Find treatments with pagination and specialty filtering.
     *
     * @param specialtyId The specialty ID (optional)
     * @param pageable Pagination information
     * @return Page of treatments
     */
    @Query("SELECT t FROM Treatment t WHERE " +
           "(:specialtyId IS NULL OR t.specialty.id = :specialtyId)")
    Page<Treatment> findBySpecialtyIdWithPagination(
            @Param("specialtyId") Long specialtyId,
            Pageable pageable);
    
    /**
     * Find treatments that have been prescribed to a specific patient.
     *
     * @param patientId The patient's ID
     * @return List of treatments prescribed to the patient
     */
    @Query("SELECT DISTINCT t FROM Treatment t JOIN t.prescriptions pt " +
           "JOIN pt.prescription p WHERE p.patient.id = :patientId")
    List<Treatment> findByPatientId(@Param("patientId") Long patientId);
    
    /**
     * Find treatments prescribed by a specific doctor.
     *
     * @param doctorId The doctor's ID
     * @return List of treatments prescribed by the doctor
     */
    @Query("SELECT DISTINCT t FROM Treatment t JOIN t.prescriptions pt " +
           "JOIN pt.prescription p WHERE p.doctor.id = :doctorId")
    List<Treatment> findByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Find treatments prescribed in a specific date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of treatments prescribed in the date range
     */
    @Query("SELECT DISTINCT t FROM Treatment t JOIN t.prescriptions pt " +
           "JOIN pt.prescription p WHERE p.issuedAt BETWEEN :startDate AND :endDate")
    List<Treatment> findByPrescriptionDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find treatments related to a specific diagnosis.
     * This finds treatments that have been prescribed for a specific diagnosis.
     *
     * @param diagnosisId The diagnosis ID
     * @return List of treatments related to the diagnosis
     */
    @Query("SELECT DISTINCT t FROM Treatment t JOIN t.prescriptions pt " +
           "JOIN pt.prescription p JOIN p.medicalRecordItems mrip " +
           "JOIN mrip.medicalRecordItem mri JOIN mri.diagnoses dmi " +
           "WHERE dmi.diagnosis.id = :diagnosisId")
    List<Treatment> findByDiagnosisId(@Param("diagnosisId") Long diagnosisId);
    
    /**
     * Count treatments by specialty.
     *
     * @return Treatment counts grouped by specialty
     */
    @Query("SELECT t.specialty.id as specialtyId, t.specialty.name as specialtyName, " +
           "COUNT(t) as count FROM Treatment t " +
           "GROUP BY t.specialty.id, t.specialty.name " +
           "ORDER BY count DESC")
    List<Object[]> countTreatmentsBySpecialty();
    
    /**
     * Find most commonly prescribed treatments.
     *
     * @return Treatment frequency statistics
     */
    @Query("SELECT t.id as treatmentId, t.name as treatmentName, COUNT(pt) as count " +
           "FROM Treatment t JOIN t.prescriptions pt " +
           "GROUP BY t.id, t.name ORDER BY count DESC")
    List<Object[]> findMostCommonTreatments();
    
    /**
     * Find most commonly prescribed treatments for a specific period.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Treatment frequency statistics for the period
     */
    @Query("SELECT t.id as treatmentId, t.name as treatmentName, COUNT(pt) as count " +
           "FROM Treatment t JOIN t.prescriptions pt JOIN pt.prescription p " +
           "WHERE p.issuedAt BETWEEN :startDate AND :endDate " +
           "GROUP BY t.id, t.name ORDER BY count DESC")
    List<Object[]> findMostCommonTreatmentsByPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find treatments that have not been prescribed in a specific period.
     * Useful for identifying potentially obsolete or rarely used treatments.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of unused treatments
     */
    @Query("SELECT t FROM Treatment t WHERE NOT EXISTS " +
           "(SELECT pt FROM PrescriptionTreatment pt JOIN pt.prescription p " +
           "WHERE pt.treatment = t AND p.issuedAt BETWEEN :startDate AND :endDate)")
    List<Treatment> findUnusedTreatments(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find treatments most commonly prescribed together with a specific treatment.
     * Useful for identifying complementary treatments.
     *
     * @param treatmentId The treatment ID
     * @return Related treatment statistics
     */
    @Query("SELECT t2.id as treatmentId, t2.name as treatmentName, COUNT(pt2) as count " +
           "FROM PrescriptionTreatment pt1 JOIN pt1.prescription p " +
           "JOIN p.treatments pt2 JOIN pt2.treatment t2 " +
           "WHERE pt1.treatment.id = :treatmentId AND pt2.treatment.id != :treatmentId " +
           "GROUP BY t2.id, t2.name ORDER BY count DESC")
    List<Object[]> findRelatedTreatments(@Param("treatmentId") Long treatmentId);
    
    /**
     * Find treatments with their average effectiveness rating.
     * Effectiveness is tracked in the Session entity.
     *
     * @return Treatment effectiveness statistics
     */
    @Query("SELECT t.id as treatmentId, t.name as treatmentName, AVG(s.effectiveness) as avgEffectiveness " +
           "FROM Treatment t JOIN t.sessions s " +
           "WHERE s.effectiveness IS NOT NULL " +
           "GROUP BY t.id, t.name ORDER BY avgEffectiveness DESC")
    List<Object[]> findTreatmentsByEffectiveness();
    
    /**
     * Find treatments by effectiveness for a specific diagnosis.
     *
     * @param diagnosisId The diagnosis ID
     * @return Treatment effectiveness statistics for the diagnosis
     */
    @Query("SELECT t.id as treatmentId, t.name as treatmentName, AVG(s.effectiveness) as avgEffectiveness " +
           "FROM Treatment t JOIN t.sessions s JOIN s.patient p " +
           "JOIN p.medicalRecord mr JOIN mr.items mri JOIN mri.diagnoses dmi " +
           "WHERE dmi.diagnosis.id = :diagnosisId AND s.effectiveness IS NOT NULL " +
           "GROUP BY t.id, t.name ORDER BY avgEffectiveness DESC")
    List<Object[]> findTreatmentsByEffectivenessForDiagnosis(@Param("diagnosisId") Long diagnosisId);
    
    /**
     * Find treatments with sessions in a specific date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of treatments with sessions in the date range
     */
    @Query("SELECT DISTINCT t FROM Treatment t JOIN t.sessions s " +
           "WHERE s.sessionDate BETWEEN :startDate AND :endDate")
    List<Treatment> findBySessionDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}