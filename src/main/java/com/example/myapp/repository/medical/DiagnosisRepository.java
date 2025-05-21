package com.example.myapp.repository.medical;

import com.example.myapp.entity.Diagnosis;
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
 * Repository for managing medical diagnoses.
 * Provides queries for diagnosis-related operations and analytics.
 */
@Repository
public interface DiagnosisRepository extends BaseRepository<Diagnosis, Long> {

    /**
     * Find a diagnosis by name (case insensitive).
     *
     * @param name The diagnosis name
     * @return The diagnosis if found
     */
    Optional<Diagnosis> findByNameIgnoreCase(String name);
    
    /**
     * Find diagnoses by specialty ID.
     *
     * @param specialtyId The specialty ID
     * @return List of diagnoses in the specialty
     */
    List<Diagnosis> findBySpecialtyId(Long specialtyId);
    
    /**
     * Find diagnoses that contain specific text in name or description.
     *
     * @param searchText The text to search for
     * @return List of matching diagnoses
     */
    @Query("SELECT d FROM Diagnosis d WHERE " +
           "LOWER(d.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Diagnosis> findByNameOrDescriptionContainingIgnoreCase(@Param("searchText") String searchText);
    
    /**
     * Find diagnoses with pagination and specialty filtering.
     *
     * @param specialtyId The specialty ID (optional)
     * @param pageable Pagination information
     * @return Page of diagnoses
     */
    @Query("SELECT d FROM Diagnosis d WHERE " +
           "(:specialtyId IS NULL OR d.specialty.id = :specialtyId)")
    Page<Diagnosis> findBySpecialtyIdWithPagination(
            @Param("specialtyId") Long specialtyId,
            Pageable pageable);
    
    /**
     * Find diagnoses that have been assigned to a specific patient.
     *
     * @param patientId The patient's ID
     * @return List of diagnoses assigned to the patient
     */
    @Query("SELECT DISTINCT d FROM Diagnosis d JOIN d.medicalRecordItems dmi " +
           "JOIN dmi.medicalRecordItem mri JOIN mri.medicalRecord mr " +
           "WHERE mr.patient.id = :patientId")
    List<Diagnosis> findByPatientId(@Param("patientId") Long patientId);
    
    /**
     * Find diagnoses made by a specific doctor.
     *
     * @param doctorId The doctor's ID
     * @return List of diagnoses made by the doctor
     */
    @Query("SELECT DISTINCT d FROM Diagnosis d JOIN d.medicalRecordItems dmi " +
           "JOIN dmi.medicalRecordItem mri WHERE mri.doctor.id = :doctorId")
    List<Diagnosis> findByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Find diagnoses made in a specific date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of diagnoses made in the date range
     */
    @Query("SELECT DISTINCT d FROM Diagnosis d JOIN d.medicalRecordItems dmi " +
           "JOIN dmi.medicalRecordItem mri WHERE mri.entryDate BETWEEN :startDate AND :endDate")
    List<Diagnosis> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find diagnoses related to a specific treatment.
     * This finds diagnoses that have been treated with a specific treatment.
     *
     * @param treatmentId The treatment ID
     * @return List of diagnoses related to the treatment
     */
    @Query("SELECT DISTINCT d FROM Diagnosis d JOIN d.medicalRecordItems dmi " +
           "JOIN dmi.medicalRecordItem mri JOIN mri.prescriptions mrip " +
           "JOIN mrip.prescription p JOIN p.treatments pt " +
           "WHERE pt.treatment.id = :treatmentId")
    List<Diagnosis> findByTreatmentId(@Param("treatmentId") Long treatmentId);
    
    /**
     * Count diagnoses by specialty.
     *
     * @return Diagnosis counts grouped by specialty
     */
    @Query("SELECT d.specialty.id as specialtyId, d.specialty.name as specialtyName, " +
           "COUNT(d) as count FROM Diagnosis d " +
           "GROUP BY d.specialty.id, d.specialty.name " +
           "ORDER BY count DESC")
    List<Object[]> countDiagnosesBySpecialty();
    
    /**
     * Find most common diagnoses based on occurrence in medical records.
     *
     * @return Diagnosis frequency statistics
     */
    @Query("SELECT d.id as diagnosisId, d.name as diagnosisName, COUNT(dmi) as count " +
           "FROM Diagnosis d JOIN d.medicalRecordItems dmi " +
           "GROUP BY d.id, d.name ORDER BY count DESC")
    List<Object[]> findMostCommonDiagnoses();
    
    /**
     * Find most common diagnoses for a specific period.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Diagnosis frequency statistics for the period
     */
    @Query("SELECT d.id as diagnosisId, d.name as diagnosisName, COUNT(dmi) as count " +
           "FROM Diagnosis d JOIN d.medicalRecordItems dmi " +
           "JOIN dmi.medicalRecordItem mri " +
           "WHERE mri.entryDate BETWEEN :startDate AND :endDate " +
           "GROUP BY d.id, d.name ORDER BY count DESC")
    List<Object[]> findMostCommonDiagnosesByPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find diagnoses that have not been made in a specific period.
     * Useful for identifying potentially obsolete or rarely used diagnoses.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of unused diagnoses
     */
    @Query("SELECT d FROM Diagnosis d WHERE NOT EXISTS " +
           "(SELECT dmi FROM DiagnosisMedItem dmi JOIN dmi.medicalRecordItem mri " +
           "WHERE dmi.diagnosis = d AND mri.entryDate BETWEEN :startDate AND :endDate)")
    List<Diagnosis> findUnusedDiagnoses(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find diagnoses most commonly associated with a specific diagnosis.
     * Useful for identifying comorbidities or related conditions.
     *
     * @param diagnosisId The diagnosis ID
     * @return Related diagnosis statistics
     */
    @Query("SELECT d2.id as diagnosisId, d2.name as diagnosisName, COUNT(dmi2) as count " +
           "FROM DiagnosisMedItem dmi1 JOIN dmi1.medicalRecordItem mri " +
           "JOIN mri.diagnoses dmi2 JOIN dmi2.diagnosis d2 " +
           "WHERE dmi1.diagnosis.id = :diagnosisId AND dmi2.diagnosis.id != :diagnosisId " +
           "GROUP BY d2.id, d2.name ORDER BY count DESC")
    List<Object[]> findRelatedDiagnoses(@Param("diagnosisId") Long diagnosisId);
    
    /**
     * Find diagnoses by age group.
     * Useful for epidemiological studies.
     *
     * @param minAge The minimum age in years
     * @param maxAge The maximum age in years
     * @return Diagnosis statistics by age group
     */
    @Query("SELECT d.id as diagnosisId, d.name as diagnosisName, COUNT(dmi) as count " +
           "FROM Diagnosis d JOIN d.medicalRecordItems dmi " +
           "JOIN dmi.medicalRecordItem mri JOIN mri.medicalRecord mr JOIN mr.patient p " +
           "WHERE FUNCTION('TIMESTAMPDIFF', YEAR, p.birthDate, CURRENT_DATE) BETWEEN :minAge AND :maxAge " +
           "GROUP BY d.id, d.name ORDER BY count DESC")
    List<Object[]> findDiagnosesByAgeGroup(
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge);
}