package com.example.myapp.repository.doctor;

import com.example.myapp.entity.DoctorSpecialty;
import com.example.myapp.entity.DoctorSpecialty.ExperienceLevel;
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
 * Repository for managing doctor specialty relationships.
 * Provides queries for analyzing doctor qualifications and expertise.
 */
@Repository
public interface DoctorSpecialtyRepository extends BaseRepository<DoctorSpecialty, Long> {

    /**
     * Find relationship by doctor ID and specialty ID.
     *
     * @param doctorId The doctor's ID
     * @param specialtyId The specialty ID
     * @return The relationship if found
     */
    Optional<DoctorSpecialty> findByDoctorIdAndSpecialtyId(Long doctorId, Long specialtyId);
    
    /**
     * Find relationships by doctor ID.
     *
     * @param doctorId The doctor's ID
     * @return List of relationships for the doctor
     */
    List<DoctorSpecialty> findByDoctorId(Long doctorId);
    
    /**
     * Find relationships by specialty ID.
     *
     * @param specialtyId The specialty ID
     * @return List of relationships for the specialty
     */
    List<DoctorSpecialty> findBySpecialtyId(Long specialtyId);
    
    /**
     * Find relationships by experience level.
     *
     * @param experienceLevel The experience level
     * @return List of relationships with the specified experience level
     */
    List<DoctorSpecialty> findByExperienceLevel(ExperienceLevel experienceLevel);
    
    /**
     * Find relationships with certification dates in a specific range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of relationships with certification dates in the range
     */
    List<DoctorSpecialty> findByCertificationDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find relationships by specialty ID and experience level.
     *
     * @param specialtyId The specialty ID
     * @param experienceLevel The experience level
     * @return List of relationships with the specified specialty and experience level
     */
    List<DoctorSpecialty> findBySpecialtyIdAndExperienceLevel(
            Long specialtyId, 
            ExperienceLevel experienceLevel);
    
    /**
     * Find relationships with pagination and filtering.
     *
     * @param specialtyId The specialty ID (optional)
     * @param experienceLevel The experience level (optional)
     * @param pageable Pagination information
     * @return Page of filtered relationships
     */
    @Query("SELECT ds FROM DoctorSpecialty ds WHERE " +
           "(:specialtyId IS NULL OR ds.specialty.id = :specialtyId) AND " +
           "(:experienceLevel IS NULL OR ds.experienceLevel = :experienceLevel)")
    Page<DoctorSpecialty> findWithFilters(
            @Param("specialtyId") Long specialtyId,
            @Param("experienceLevel") ExperienceLevel experienceLevel,
            Pageable pageable);
    
    /**
     * Find relationships for doctors who treat a specific diagnosis.
     *
     * @param diagnosisId The diagnosis ID
     * @return List of relationships for doctors who treat the diagnosis
     */
    @Query("SELECT DISTINCT ds FROM DoctorSpecialty ds JOIN ds.specialty s " +
           "JOIN s.diagnoses d WHERE d.id = :diagnosisId")
    List<DoctorSpecialty> findByDiagnosisId(@Param("diagnosisId") Long diagnosisId);
    
    /**
     * Find relationships for doctors who performed a specific treatment.
     *
     * @param treatmentId The treatment ID
     * @return List of relationships for doctors who performed the treatment
     */
    @Query("SELECT DISTINCT ds FROM DoctorSpecialty ds JOIN ds.doctor d " +
           "JOIN d.prescriptions p JOIN p.treatments pt " +
           "WHERE pt.treatment.id = :treatmentId")
    List<DoctorSpecialty> findByTreatmentId(@Param("treatmentId") Long treatmentId);
    
    /**
     * Count relationships by specialty.
     *
     * @return Relationship counts grouped by specialty
     */
    @Query("SELECT ds.specialty.id as specialtyId, ds.specialty.name as specialtyName, " +
           "COUNT(ds) as count FROM DoctorSpecialty ds " +
           "GROUP BY ds.specialty.id, ds.specialty.name ORDER BY count DESC")
    List<Object[]> countBySpecialty();
    
    /**
     * Count relationships by experience level.
     *
     * @return Relationship counts grouped by experience level
     */
    @Query("SELECT ds.experienceLevel as experienceLevel, COUNT(ds) as count " +
           "FROM DoctorSpecialty ds GROUP BY ds.experienceLevel ORDER BY ds.experienceLevel")
    List<Object[]> countByExperienceLevel();
    
    /**
     * Find most experienced doctors in a specialty.
     *
     * @param specialtyId The specialty ID
     * @return List of relationships for experienced doctors in the specialty
     */
    @Query("SELECT ds FROM DoctorSpecialty ds WHERE ds.specialty.id = :specialtyId " +
           "AND ds.experienceLevel IN ('SENIOR', 'CONSULTANT') " +
           "ORDER BY ds.experienceLevel DESC, ds.certificationDate")
    List<DoctorSpecialty> findMostExperiencedDoctorsBySpecialty(@Param("specialtyId") Long specialtyId);
    
    /**
     * Find recently certified doctors in a specialty.
     *
     * @param specialtyId The specialty ID
     * @param sinceDate The cutoff date
     * @return List of relationships for recently certified doctors
     */
    @Query("SELECT ds FROM DoctorSpecialty ds WHERE ds.specialty.id = :specialtyId " +
           "AND ds.certificationDate >= :sinceDate ORDER BY ds.certificationDate DESC")
    List<DoctorSpecialty> findRecentlyCertifiedDoctors(
            @Param("specialtyId") Long specialtyId,
            @Param("sinceDate") LocalDate sinceDate);
    
       /**
 * Find average certification age by specialty.
 * This calculates how long doctors have been certified in each specialty.
 *
 * @return Average certification age by specialty
 */
@Query("SELECT ds.specialty.id as specialtyId, ds.specialty.name as specialtyName, " +
       "AVG(CAST(FUNCTION('DATEDIFF', CURRENT_DATE, ds.certificationDate) AS double)) / 365.25 as avgYears " +
       "FROM DoctorSpecialty ds GROUP BY ds.specialty.id, ds.specialty.name " +
       "ORDER BY avgYears DESC")
List<Object[]> findAverageCertificationAgeBySpecialty();
    
    /**
     * Find doctors with multiple specialties.
     *
     * @param minSpecialties The minimum number of specialties
     * @return Doctor specialty counts
     */
    @Query("SELECT ds.doctor.id as doctorId, ds.doctor.firstName as firstName, " +
           "ds.doctor.lastName as lastName, COUNT(ds) as specialtyCount " +
           "FROM DoctorSpecialty ds GROUP BY ds.doctor.id, ds.doctor.firstName, ds.doctor.lastName " +
           "HAVING COUNT(ds) >= :minSpecialties ORDER BY specialtyCount DESC")
    List<Object[]> findDoctorsWithMultipleSpecialties(@Param("minSpecialties") Long minSpecialties);
    
    /**
     * Find relationships by specialty and appointment count.
     * Identifies active doctors in a specialty based on appointment volume.
     *
     * @param specialtyId The specialty ID
     * @param minAppointments The minimum number of appointments
     * @return Doctor appointment statistics
     */
    @Query("SELECT ds, COUNT(a) as appointmentCount FROM DoctorSpecialty ds " +
           "JOIN ds.doctor d JOIN d.schedules s JOIN s.appointments a " +
           "WHERE ds.specialty.id = :specialtyId AND a.specialty.id = :specialtyId " +
           "GROUP BY ds HAVING COUNT(a) >= :minAppointments " +
           "ORDER BY appointmentCount DESC")
    List<Object[]> findBySpecialtyAndMinAppointments(
            @Param("specialtyId") Long specialtyId,
            @Param("minAppointments") Long minAppointments);
    
    /**
     * Find experience level distribution by specialty.
     *
     * @return Experience level counts by specialty
     */
    @Query("SELECT ds.specialty.id as specialtyId, ds.specialty.name as specialtyName, " +
           "ds.experienceLevel as experienceLevel, COUNT(ds) as count " +
           "FROM DoctorSpecialty ds " +
           "GROUP BY ds.specialty.id, ds.specialty.name, ds.experienceLevel " +
           "ORDER BY ds.specialty.name, ds.experienceLevel")
    List<Object[]> findExperienceLevelDistributionBySpecialty();
    
    /**
     * Find certification dates for doctors in a specialty.
     * Useful for tracking certification history.
     *
     * @param specialtyId The specialty ID
     * @return Certification date distribution
     */
    @Query("SELECT " +
           "FUNCTION('YEAR', ds.certificationDate) as certificationYear, " +
           "COUNT(ds) as count FROM DoctorSpecialty ds " +
           "WHERE ds.specialty.id = :specialtyId " +
           "GROUP BY certificationYear ORDER BY certificationYear")
    List<Object[]> findCertificationYearDistribution(@Param("specialtyId") Long specialtyId);
}