package com.example.myapp.repository.prescription;

import com.example.myapp.entity.PrescriptionTreatment;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing prescription to treatment relationships.
 * Provides queries for analyzing treatment prescribing patterns.
 */
@Repository
public interface PrescriptionTreatmentRepository extends BaseRepository<PrescriptionTreatment, Long> {

    /**
     * Find relationship by prescription ID and treatment ID.
     *
     * @param prescriptionId The prescription ID
     * @param treatmentId The treatment ID
     * @return The relationship if found
     */
    Optional<PrescriptionTreatment> findByPrescriptionIdAndTreatmentId(
            Long prescriptionId, 
            Long treatmentId);
    
    /**
     * Find relationships by prescription ID.
     *
     * @param prescriptionId The prescription ID
     * @return List of relationships for the prescription
     */
    List<PrescriptionTreatment> findByPrescriptionId(Long prescriptionId);
    
    /**
     * Find relationships by treatment ID.
     *
     * @param treatmentId The treatment ID
     * @return List of relationships for the treatment
     */
    List<PrescriptionTreatment> findByTreatmentId(Long treatmentId);
    
    /**
     * Find relationships for a specific patient.
     *
     * @param patientId The patient's ID
     * @return List of relationships for the patient
     */
    @Query("SELECT pt FROM PrescriptionTreatment pt JOIN pt.prescription p " +
           "WHERE p.patient.id = :patientId")
    List<PrescriptionTreatment> findByPatientId(@Param("patientId") Long patientId);
    
    /**
     * Find relationships created by a specific doctor.
     *
     * @param doctorId The doctor's ID
     * @return List of relationships created by the doctor
     */
    @Query("SELECT pt FROM PrescriptionTreatment pt JOIN pt.prescription p " +
           "WHERE p.doctor.id = :doctorId")
    List<PrescriptionTreatment> findByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Find relationships created in a date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of relationships created in the date range
     */
    @Query("SELECT pt FROM PrescriptionTreatment pt JOIN pt.prescription p " +
           "WHERE p.issuedAt BETWEEN :startDate AND :endDate")
    List<PrescriptionTreatment> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find relationships for a specific diagnosis.
     * This finds treatments prescribed for a specific diagnosis through medical record items.
     *
     * @param diagnosisId The diagnosis ID
     * @return List of relationships for the diagnosis
     */
    @Query("SELECT pt FROM PrescriptionTreatment pt JOIN pt.prescription p " +
           "JOIN p.medicalRecordItems mrip JOIN mrip.medicalRecordItem mri " +
           "JOIN mri.diagnoses dmi WHERE dmi.diagnosis.id = :diagnosisId")
    List<PrescriptionTreatment> findByDiagnosisId(@Param("diagnosisId") Long diagnosisId);
    
    /**
     * Find relationships for a specific specialty through prescription.
     *
     * @param specialtyId The specialty ID
     * @return List of relationships for the specialty
     */
    @Query("SELECT pt FROM PrescriptionTreatment pt JOIN pt.prescription p " +
           "JOIN p.specialties ps WHERE ps.specialty.id = :specialtyId")
    List<PrescriptionTreatment> findByPrescriptionSpecialtyId(@Param("specialtyId") Long specialtyId);
    
    /**
     * Find relationships for a specific specialty through treatment.
     *
     * @param specialtyId The specialty ID
     * @return List of relationships for the specialty
     */
    @Query("SELECT pt FROM PrescriptionTreatment pt JOIN pt.treatment t " +
           "WHERE t.specialty.id = :specialtyId")
    List<PrescriptionTreatment> findByTreatmentSpecialtyId(@Param("specialtyId") Long specialtyId);
    
    /**
     * Find relationships associated with a specific appointment.
     *
     * @param appointmentId The appointment ID
     * @return List of relationships for the appointment
     */
    @Query("SELECT pt FROM PrescriptionTreatment pt JOIN pt.prescription p " +
           "WHERE p.appointment.id = :appointmentId")
    List<PrescriptionTreatment> findByAppointmentId(@Param("appointmentId") Long appointmentId);
    
    /**
     * Count relationships by treatment.
     *
     * @return Relationship counts grouped by treatment
     */
    @Query("SELECT pt.treatment.id as treatmentId, pt.treatment.name as treatmentName, " +
           "COUNT(pt) as count FROM PrescriptionTreatment pt " +
           "GROUP BY pt.treatment.id, pt.treatment.name ORDER BY count DESC")
    List<Object[]> countByTreatment();
    
    /**
     * Count relationships by doctor.
     *
     * @return Relationship counts grouped by doctor
     */
    @Query("SELECT p.doctor.id as doctorId, p.doctor.firstName as firstName, " +
           "p.doctor.lastName as lastName, COUNT(pt) as count " +
           "FROM PrescriptionTreatment pt JOIN pt.prescription p " +
           "GROUP BY p.doctor.id, p.doctor.firstName, p.doctor.lastName " +
           "ORDER BY count DESC")
    List<Object[]> countByDoctor();
    
    /**
     * Count relationships by specialty from treatment side.
     *
     * @return Relationship counts grouped by specialty
     */
    @Query("SELECT t.specialty.id as specialtyId, t.specialty.name as specialtyName, " +
           "COUNT(pt) as count FROM PrescriptionTreatment pt JOIN pt.treatment t " +
           "GROUP BY t.specialty.id, t.specialty.name ORDER BY count DESC")
    List<Object[]> countByTreatmentSpecialty();
    
    /**
     * Find treatment frequency trends over time.
     *
     * @param treatmentId The treatment ID
     * @param interval The time interval ('MONTH', 'QUARTER', 'YEAR')
     * @return Treatment frequency data over time
     */
    @Query("SELECT FUNCTION('DATE_FORMAT', p.issuedAt, " +
           "CASE :interval " +
           "WHEN 'MONTH' THEN '%Y-%m' " +
           "WHEN 'QUARTER' THEN '%Y-Q%q' " +
           "WHEN 'YEAR' THEN '%Y' " +
           "ELSE '%Y-%m' END) as timeInterval, " +
           "COUNT(pt) as count FROM PrescriptionTreatment pt JOIN pt.prescription p " +
           "WHERE pt.treatment.id = :treatmentId " +
           "GROUP BY timeInterval ORDER BY timeInterval")
    List<Object[]> findTreatmentTrendsOverTime(
            @Param("treatmentId") Long treatmentId,
            @Param("interval") String interval);
    
    /**
     * Find treatments most frequently prescribed together.
     * Useful for identifying complementary treatments.
     *
     * @param minOccurrences The minimum number of co-occurrences
     * @return Treatment pair statistics
     */
    @Query("SELECT t1.id as treatment1Id, t1.name as treatment1Name, " +
           "t2.id as treatment2Id, t2.name as treatment2Name, COUNT(p) as count " +
           "FROM PrescriptionTreatment pt1 JOIN pt1.prescription p JOIN pt1.treatment t1, " +
           "PrescriptionTreatment pt2 JOIN pt2.prescription p2 JOIN pt2.treatment t2 " +
           "WHERE p.id = p2.id AND t1.id < t2.id " +
           "GROUP BY t1.id, t1.name, t2.id, t2.name " +
           "HAVING COUNT(p) >= :minOccurrences " +
           "ORDER BY count DESC")
    List<Object[]> findFrequentTreatmentPairs(@Param("minOccurrences") Long minOccurrences);
    
    /**
     * Find treatment prescription patterns by patient demographics.
     * This example looks at age groups.
     *
     * @param treatmentId The treatment ID
     * @return Age group distribution for the treatment
     */
    @Query("SELECT " +
           "CASE " +
           "WHEN FUNCTION('TIMESTAMPDIFF', YEAR, p.birthDate, CURRENT_DATE) < 18 THEN 'Under 18' " +
           "WHEN FUNCTION('TIMESTAMPDIFF', YEAR, p.birthDate, CURRENT_DATE) BETWEEN 18 AND 30 THEN '18-30' " +
           "WHEN FUNCTION('TIMESTAMPDIFF', YEAR, p.birthDate, CURRENT_DATE) BETWEEN 31 AND 50 THEN '31-50' " +
           "WHEN FUNCTION('TIMESTAMPDIFF', YEAR, p.birthDate, CURRENT_DATE) BETWEEN 51 AND 65 THEN '51-65' " +
           "ELSE 'Over 65' END as ageGroup, " +
           "COUNT(pt) as count FROM PrescriptionTreatment pt " +
           "JOIN pt.prescription pr JOIN pr.patient p " +
           "WHERE pt.treatment.id = :treatmentId " +
           "GROUP BY ageGroup ORDER BY ageGroup")
    List<Object[]> findTreatmentPatternsByAgeGroup(@Param("treatmentId") Long treatmentId);
    
    /**
     * Find treatments with associated sessions and average effectiveness.
     *
     * @return Treatment effectiveness statistics
     */
    @Query("SELECT pt.treatment.id as treatmentId, pt.treatment.name as treatmentName, " +
           "AVG(s.effectiveness) as avgEffectiveness, COUNT(s) as sessionCount " +
           "FROM PrescriptionTreatment pt JOIN pt.treatment t JOIN t.sessions s " +
           "WHERE s.effectiveness IS NOT NULL " +
           "GROUP BY pt.treatment.id, pt.treatment.name " +
           "HAVING COUNT(s) >= 3 " +
           "ORDER BY avgEffectiveness DESC")
    List<Object[]> findTreatmentsWithEffectivenessRatings();
}