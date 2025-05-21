package com.example.myapp.repository.medical;

import com.example.myapp.entity.Session;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for managing treatment sessions.
 * Provides queries for tracking and analyzing treatment effectiveness.
 */
@Repository
public interface SessionRepository extends BaseRepository<Session, Long> {

    /**
     * Find sessions for a specific treatment.
     *
     * @param treatmentId The treatment ID
     * @return List of sessions for the treatment
     */
    List<Session> findByTreatmentId(Long treatmentId);
    
    /**
     * Find sessions for a specific patient.
     *
     * @param patientId The patient's ID
     * @return List of sessions for the patient
     */
    List<Session> findByPatientId(Long patientId);
    
    /**
     * Find sessions on a specific date.
     *
     * @param sessionDate The session date
     * @return List of sessions on the date
     */
    List<Session> findBySessionDate(LocalDate sessionDate);
    
    /**
     * Find sessions in a date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of sessions in the date range
     */
    List<Session> findBySessionDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find sessions for a specific patient and treatment.
     *
     * @param patientId The patient's ID
     * @param treatmentId The treatment ID
     * @return List of sessions for the patient and treatment
     */
    List<Session> findByPatientIdAndTreatmentId(Long patientId, Long treatmentId);
    
    /**
     * Find sessions with a minimum effectiveness rating.
     *
     * @param minEffectiveness The minimum effectiveness value
     * @return List of sessions with at least the specified effectiveness
     */
    List<Session> findByEffectivenessGreaterThanEqual(Integer minEffectiveness);
    
    /**
     * Find sessions with pagination.
     *
     * @param patientId The patient's ID (optional)
     * @param treatmentId The treatment ID (optional)
     * @param pageable Pagination information
     * @return Page of sessions
     */
    @Query("SELECT s FROM Session s WHERE " +
           "(:patientId IS NULL OR s.patient.id = :patientId) AND " +
           "(:treatmentId IS NULL OR s.treatment.id = :treatmentId)")
    Page<Session> findWithFilters(
            @Param("patientId") Long patientId,
            @Param("treatmentId") Long treatmentId,
            Pageable pageable);
    
    /**
     * Count sessions by treatment.
     *
     * @return Session counts grouped by treatment
     */
    @Query("SELECT s.treatment.id as treatmentId, s.treatment.name as treatmentName, " +
           "COUNT(s) as count FROM Session s " +
           "GROUP BY s.treatment.id, s.treatment.name " +
           "ORDER BY count DESC")
    List<Object[]> countSessionsByTreatment();
    
    /**
     * Count sessions by patient.
     *
     * @return Session counts grouped by patient
     */
    @Query("SELECT s.patient.id as patientId, s.patient.firstName as firstName, " +
           "s.patient.lastName as lastName, COUNT(s) as count FROM Session s " +
           "GROUP BY s.patient.id, s.patient.firstName, s.patient.lastName " +
           "ORDER BY count DESC")
    List<Object[]> countSessionsByPatient();
    
    /**
     * Calculate average effectiveness by treatment.
     *
     * @return Average effectiveness grouped by treatment
     */
    @Query("SELECT s.treatment.id as treatmentId, s.treatment.name as treatmentName, " +
           "AVG(s.effectiveness) as avgEffectiveness FROM Session s " +
           "WHERE s.effectiveness IS NOT NULL " +
           "GROUP BY s.treatment.id, s.treatment.name " +
           "ORDER BY avgEffectiveness DESC")
    List<Object[]> averageEffectivenessByTreatment();
    
    /**
     * Find recent sessions for a specific patient with pagination.
     *
     * @param patientId The patient's ID
     * @param pageable Pagination information
     * @return Page of recent sessions for the patient
     */
    @Query("SELECT s FROM Session s WHERE s.patient.id = :patientId " +
           "ORDER BY s.sessionDate DESC")
    Page<Session> findRecentSessionsByPatientId(
            @Param("patientId") Long patientId,
            Pageable pageable);
    
    /**
     * Find sessions related to a specific diagnosis.
     * This finds sessions for treatments that have been prescribed for a diagnosis.
     *
     * @param diagnosisId The diagnosis ID
     * @return List of sessions related to the diagnosis
     */
    @Query("SELECT DISTINCT s FROM Session s JOIN s.treatment t " +
           "JOIN t.prescriptions pt JOIN pt.prescription p " +
           "JOIN p.medicalRecordItems mrip JOIN mrip.medicalRecordItem mri " +
           "JOIN mri.diagnoses dmi WHERE dmi.diagnosis.id = :diagnosisId")
    List<Session> findByDiagnosisId(@Param("diagnosisId") Long diagnosisId);
    
    /**
     * Find sessions with observations containing specific text.
     *
     * @param searchText The text to search for
     * @return List of sessions with matching observations
     */
    @Query("SELECT s FROM Session s WHERE LOWER(s.observations) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Session> findByObservationsContainingIgnoreCase(@Param("searchText") String searchText);
    
    /**
     * Track effectiveness improvement over time for a specific treatment.
     *
     * @param treatmentId The treatment ID
     * @return Effectiveness data grouped by session date
     */
    @Query("SELECT s.sessionDate as sessionDate, AVG(s.effectiveness) as avgEffectiveness " +
           "FROM Session s WHERE s.treatment.id = :treatmentId AND s.effectiveness IS NOT NULL " +
           "GROUP BY s.sessionDate ORDER BY s.sessionDate")
    List<Object[]> trackEffectivenessOverTime(@Param("treatmentId") Long treatmentId);
}