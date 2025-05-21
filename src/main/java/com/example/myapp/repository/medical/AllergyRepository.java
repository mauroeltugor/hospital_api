package com.example.myapp.repository.medical;

import com.example.myapp.entity.Allergy;
import com.example.myapp.entity.Allergy.Severity;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing patient allergies.
 * Provides queries for allergy tracking and analysis.
 */
@Repository
public interface AllergyRepository extends BaseRepository<Allergy, Long> {

    /**
     * Find an allergy by name (case insensitive).
     *
     * @param name The allergy name
     * @return The allergy if found
     */
    Optional<Allergy> findByNameIgnoreCase(String name);
    
    /**
     * Find allergies by severity level.
     *
     * @param severity The severity level
     * @return List of allergies with the specified severity
     */
    List<Allergy> findBySeverity(Severity severity);
    
    /**
     * Find allergies that contain specific text in name or notes.
     *
     * @param searchText The text to search for
     * @return List of matching allergies
     */
    @Query("SELECT a FROM Allergy a WHERE " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(a.notes) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Allergy> findByNameOrNotesContainingIgnoreCase(@Param("searchText") String searchText);
    
    /**
     * Find allergies with pagination and severity filtering.
     *
     * @param severity The severity level (optional)
     * @param pageable Pagination information
     * @return Page of allergies
     */
    @Query("SELECT a FROM Allergy a WHERE " +
           "(:severity IS NULL OR a.severity = :severity)")
    Page<Allergy> findBySeverityWithPagination(
            @Param("severity") Severity severity,
            Pageable pageable);
    
    /**
     * Find allergies that a specific patient has.
     *
     * @param patientId The patient's ID
     * @return List of allergies for the patient
     */
    @Query("SELECT a FROM Allergy a JOIN a.patients pa WHERE pa.patient.id = :patientId")
    List<Allergy> findByPatientId(@Param("patientId") Long patientId);
    
    /**
     * Find allergies with a minimum number of patients.
     * Useful for identifying common allergens.
     *
     * @param minPatients The minimum number of patients
     * @return List of common allergies
     */
    @Query("SELECT a FROM Allergy a JOIN a.patients pa " +
           "GROUP BY a HAVING COUNT(pa) >= :minPatients")
    List<Allergy> findCommonAllergies(@Param("minPatients") Long minPatients);
    
    /**
     * Count patients by allergy.
     *
     * @return Patient counts grouped by allergy
     */
    @Query("SELECT a.id as allergyId, a.name as allergyName, COUNT(pa) as count " +
           "FROM Allergy a JOIN a.patients pa " +
           "GROUP BY a.id, a.name ORDER BY count DESC")
    List<Object[]> countPatientsByAllergy();
    
    /**
     * Count allergies by severity.
     *
     * @return Allergy counts grouped by severity
     */
    @Query("SELECT a.severity as severity, COUNT(a) as count FROM Allergy a " +
           "GROUP BY a.severity ORDER BY a.severity")
    List<Object[]> countAllergiesBySeverity();
    
    /**
     * Find potentially related allergies based on patient overlap.
     * This finds allergies that are commonly found together in patients.
     *
     * @param allergyId The allergy ID
     * @return Related allergy statistics
     */
    @Query("SELECT a2.id as allergyId, a2.name as allergyName, COUNT(pa2) as count " +
           "FROM PatientAllergy pa1 JOIN pa1.patient p " +
           "JOIN p.allergies pa2 JOIN pa2.allergy a2 " +
           "WHERE pa1.allergy.id = :allergyId AND pa2.allergy.id != :allergyId " +
           "GROUP BY a2.id, a2.name ORDER BY count DESC")
    List<Object[]> findRelatedAllergies(@Param("allergyId") Long allergyId);
    
    /**
     * Find patients with severe or life-threatening allergies.
     *
     * @return List of patients with severe allergies
     */
    @Query("SELECT DISTINCT pa.patient.id as patientId, pa.patient.firstName as firstName, " +
           "pa.patient.lastName as lastName, a.name as allergyName, a.severity as severity " +
           "FROM PatientAllergy pa JOIN pa.allergy a " +
           "WHERE a.severity IN ('SEVERE', 'LIFE_THREATENING') " +
           "ORDER BY pa.patient.lastName, pa.patient.firstName")
    List<Object[]> findPatientsWithSevereAllergies();
    
    /**
     * Find allergies that should be considered during prescription.
     * This might be all allergies, but the query structure allows for filtering.
     *
     * @return List of prescription-relevant allergies
     */
    @Query("SELECT a FROM Allergy a WHERE a.severity IN ('MODERATE', 'SEVERE', 'LIFE_THREATENING')")
    List<Allergy> findPrescriptionRelevantAllergies();
    
    /**
     * Count allergies by severity for a specific patient.
     *
     * @param patientId The patient's ID
     * @return Allergy severity counts for the patient
     */
    @Query("SELECT a.severity as severity, COUNT(pa) as count " +
           "FROM PatientAllergy pa JOIN pa.allergy a " +
           "WHERE pa.patient.id = :patientId " +
           "GROUP BY a.severity ORDER BY a.severity")
    List<Object[]> countAllergiesBySeverityForPatient(@Param("patientId") Long patientId);
}