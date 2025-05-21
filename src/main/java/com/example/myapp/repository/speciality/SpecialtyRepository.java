package com.example.myapp.repository.speciality;

import com.example.myapp.entity.Specialty;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing medical specialties.
 * Provides queries for specialty-related operations and analytics.
 */
@Repository
public interface SpecialtyRepository extends BaseRepository<Specialty, Long> {

    /**
     * Find a specialty by its name (case insensitive).
     *
     * @param name The specialty name
     * @return The specialty if found
     */
    Optional<Specialty> findByNameIgnoreCase(String name);
    
    /**
     * Find a specialty with all related diagnoses eager loaded.
     *
     * @param id The specialty ID
     * @return The specialty with diagnoses if found
     */
    @Query("SELECT s FROM Specialty s LEFT JOIN FETCH s.diagnoses WHERE s.id = :id")
    Optional<Specialty> findByIdWithDiagnoses(@Param("id") Long id);
    
    /**
     * Find a specialty with all related treatments eager loaded.
     *
     * @param id The specialty ID
     * @return The specialty with treatments if found
     */
    @Query("SELECT s FROM Specialty s LEFT JOIN FETCH s.treatments WHERE s.id = :id")
    Optional<Specialty> findByIdWithTreatments(@Param("id") Long id);
    
    /**
     * Find specialties that contain a specific text in name or description.
     *
     * @param searchText The text to search for
     * @return List of matching specialties
     */
    @Query("SELECT s FROM Specialty s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Specialty> findByNameOrDescriptionContainingIgnoreCase(@Param("searchText") String searchText);
    
    /**
     * Find specialties with a minimum number of doctors.
     *
     * @param minDoctors The minimum number of doctors
     * @return List of specialties with at least the specified number of doctors
     */
    @Query("SELECT s FROM Specialty s JOIN s.doctors ds " +
           "GROUP BY s HAVING COUNT(ds) >= :minDoctors")
    List<Specialty> findSpecialtiesWithMinimumDoctors(@Param("minDoctors") Long minDoctors);
    
    /**
     * Find specialties with a minimum number of diagnoses.
     *
     * @param minDiagnoses The minimum number of diagnoses
     * @return List of specialties with at least the specified number of diagnoses
     */
    @Query("SELECT s FROM Specialty s JOIN s.diagnoses d " +
           "GROUP BY s HAVING COUNT(d) >= :minDiagnoses")
    List<Specialty> findSpecialtiesWithMinimumDiagnoses(@Param("minDiagnoses") Long minDiagnoses);
    
    /**
     * Find specialties with a minimum number of treatments.
     *
     * @param minTreatments The minimum number of treatments
     * @return List of specialties with at least the specified number of treatments
     */
    @Query("SELECT s FROM Specialty s JOIN s.treatments t " +
           "GROUP BY s HAVING COUNT(t) >= :minTreatments")
    List<Specialty> findSpecialtiesWithMinimumTreatments(@Param("minTreatments") Long minTreatments);
    
    /**
     * Find specialties handled by a specific doctor.
     *
     * @param doctorId The doctor's ID
     * @return List of specialties for the doctor
     */
    @Query("SELECT s FROM Specialty s JOIN s.doctors ds WHERE ds.doctor.id = :doctorId")
    List<Specialty> findByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Find specialties associated with a specific diagnosis.
     *
     * @param diagnosisId The diagnosis ID
     * @return List of specialties associated with the diagnosis
     */
    @Query("SELECT s FROM Specialty s JOIN s.diagnoses d WHERE d.id = :diagnosisId")
    List<Specialty> findByDiagnosisId(@Param("diagnosisId") Long diagnosisId);
    
    /**
     * Find specialties associated with a specific treatment.
     *
     * @param treatmentId The treatment ID
     * @return List of specialties associated with the treatment
     */
    @Query("SELECT s FROM Specialty s JOIN s.treatments t WHERE t.id = :treatmentId")
    List<Specialty> findByTreatmentId(@Param("treatmentId") Long treatmentId);
    
    /**
     * Count doctors by specialty.
     *
     * @return Doctor counts grouped by specialty
     */
    @Query("SELECT s.id as specialtyId, s.name as specialtyName, COUNT(ds) as count " +
           "FROM Specialty s JOIN s.doctors ds " +
           "GROUP BY s.id, s.name ORDER BY count DESC")
    List<Object[]> countDoctorsBySpecialty();
    
    /**
     * Count diagnoses by specialty.
     *
     * @return Diagnosis counts grouped by specialty
     */
    @Query("SELECT s.id as specialtyId, s.name as specialtyName, COUNT(d) as count " +
           "FROM Specialty s JOIN s.diagnoses d " +
           "GROUP BY s.id, s.name ORDER BY count DESC")
    List<Object[]> countDiagnosesBySpecialty();
    
    /**
     * Count treatments by specialty.
     *
     * @return Treatment counts grouped by specialty
     */
    @Query("SELECT s.id as specialtyId, s.name as specialtyName, COUNT(t) as count " +
           "FROM Specialty s JOIN s.treatments t " +
           "GROUP BY s.id, s.name ORDER BY count DESC")
    List<Object[]> countTreatmentsBySpecialty();
    
    /**
     * Find specialties with recently certified doctors.
     *
     * @param sinceDate The cutoff date for certification
     * @return List of specialties with recently certified doctors
     */
    @Query("SELECT DISTINCT s FROM Specialty s JOIN s.doctors ds " +
           "WHERE ds.certificationDate >= :sinceDate")
    List<Specialty> findSpecialtiesWithRecentCertifications(@Param("sinceDate") LocalDate sinceDate);
    
    /**
     * Find most active specialties based on appointment count.
     *
     * @return Specialty appointment count statistics
     */
    @Query("SELECT s.id as specialtyId, s.name as specialtyName, COUNT(a) as count " +
           "FROM Specialty s JOIN Appointment a ON a.specialty.id = s.id " +
           "GROUP BY s.id, s.name ORDER BY count DESC")
    List<Object[]> findMostActiveSpecialties();
    
    /**
     * Find specialties by prescription frequency.
     *
     * @return Specialty prescription count statistics
     */
    @Query("SELECT s.id as specialtyId, s.name as specialtyName, COUNT(sp) as count " +
           "FROM Specialty s JOIN s.prescriptions sp " +
           "GROUP BY s.id, s.name ORDER BY count DESC")
    List<Object[]> findSpecialtiesByPrescriptionFrequency();
}