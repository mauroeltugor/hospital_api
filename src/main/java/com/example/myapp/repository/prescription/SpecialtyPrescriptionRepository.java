package com.example.myapp.repository.prescription;

import com.example.myapp.entity.SpecialtyPrescription;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing specialty to prescription relationships.
 * Provides queries for analyzing prescription patterns across medical specialties.
 */
@Repository
public interface SpecialtyPrescriptionRepository extends BaseRepository<SpecialtyPrescription, Long> {

    /**
     * Find relationship by specialty ID and prescription ID.
     *
     * @param specialtyId The specialty ID
     * @param prescriptionId The prescription ID
     * @return The relationship if found
     */
    Optional<SpecialtyPrescription> findBySpecialtyIdAndPrescriptionId(
            Long specialtyId, 
            Long prescriptionId);
    
    /**
     * Find relationships by prescription ID.
     *
     * @param prescriptionId The prescription ID
     * @return List of relationships for the prescription
     */
    List<SpecialtyPrescription> findByPrescriptionId(Long prescriptionId);
    
    /**
     * Find relationships by specialty ID.
     *
     * @param specialtyId The specialty ID
     * @return List of relationships for the specialty
     */
    List<SpecialtyPrescription> findBySpecialtyId(Long specialtyId);
    
    /**
     * Find relationships for a specific patient.
     *
     * @param patientId The patient's ID
     * @return List of relationships for the patient
     */
    @Query("SELECT sp FROM SpecialtyPrescription sp JOIN sp.prescription p " +
           "WHERE p.patient.id = :patientId")
    List<SpecialtyPrescription> findByPatientId(@Param("patientId") Long patientId);
    
    /**
     * Find relationships created by a specific doctor.
     *
     * @param doctorId The doctor's ID
     * @return List of relationships created by the doctor
     */
    @Query("SELECT sp FROM SpecialtyPrescription sp JOIN sp.prescription p " +
           "WHERE p.doctor.id = :doctorId")
    List<SpecialtyPrescription> findByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Find relationships created in a date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of relationships created in the date range
     */
    @Query("SELECT sp FROM SpecialtyPrescription sp JOIN sp.prescription p " +
           "WHERE p.issuedAt BETWEEN :startDate AND :endDate")
    List<SpecialtyPrescription> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find relationships associated with a specific diagnosis.
     *
     * @param diagnosisId The diagnosis ID
     * @return List of relationships associated with the diagnosis
     */
    @Query("SELECT sp FROM SpecialtyPrescription sp JOIN sp.prescription p " +
           "JOIN p.medicalRecordItems mrip JOIN mrip.medicalRecordItem mri " +
           "JOIN mri.diagnoses dmi WHERE dmi.diagnosis.id = :diagnosisId")
    List<SpecialtyPrescription> findByDiagnosisId(@Param("diagnosisId") Long diagnosisId);
    
    /**
     * Find relationships associated with a specific treatment.
     *
     * @param treatmentId The treatment ID
     * @return List of relationships associated with the treatment
     */
    @Query("SELECT sp FROM SpecialtyPrescription sp JOIN sp.prescription p " +
           "JOIN p.treatments pt WHERE pt.treatment.id = :treatmentId")
    List<SpecialtyPrescription> findByTreatmentId(@Param("treatmentId") Long treatmentId);
    
    /**
     * Find relationships associated with a specific appointment.
     *
     * @param appointmentId The appointment ID
     * @return List of relationships for the appointment
     */
    @Query("SELECT sp FROM SpecialtyPrescription sp JOIN sp.prescription p " +
           "WHERE p.appointment.id = :appointmentId")
    List<SpecialtyPrescription> findByAppointmentId(@Param("appointmentId") Long appointmentId);
    
    /**
     * Count relationships by specialty.
     *
     * @return Relationship counts grouped by specialty
     */
    @Query("SELECT sp.specialty.id as specialtyId, sp.specialty.name as specialtyName, " +
           "COUNT(sp) as count FROM SpecialtyPrescription sp " +
           "GROUP BY sp.specialty.id, sp.specialty.name ORDER BY count DESC")
    List<Object[]> countBySpecialty();
    
    /**
     * Count relationships by doctor.
     *
     * @return Relationship counts grouped by doctor
     */
    @Query("SELECT p.doctor.id as doctorId, p.doctor.firstName as firstName, " +
           "p.doctor.lastName as lastName, COUNT(sp) as count " +
           "FROM SpecialtyPrescription sp JOIN sp.prescription p " +
           "GROUP BY p.doctor.id, p.doctor.firstName, p.doctor.lastName " +
           "ORDER BY count DESC")
    List<Object[]> countByDoctor();
    
    /**
     * Find prescription patterns by specialty over time.
     *
     * @param specialtyId The specialty ID
     * @param interval The time interval ('MONTH', 'QUARTER', 'YEAR')
     * @return Prescription volume data over time
     */
    @Query("SELECT FUNCTION('DATE_FORMAT', p.issuedAt, " +
           "CASE :interval " +
           "WHEN 'MONTH' THEN '%Y-%m' " +
           "WHEN 'QUARTER' THEN '%Y-Q%q' " +
           "WHEN 'YEAR' THEN '%Y' " +
           "ELSE '%Y-%m' END) as timeInterval, " +
           "COUNT(sp) as count FROM SpecialtyPrescription sp JOIN sp.prescription p " +
           "WHERE sp.specialty.id = :specialtyId " +
           "GROUP BY timeInterval ORDER BY timeInterval")
    List<Object[]> findPrescriptionTrendsOverTime(
            @Param("specialtyId") Long specialtyId,
            @Param("interval") String interval);
    
    /**
     * Find specialties most frequently associated together in prescriptions.
     * Useful for identifying cross-specialty collaboration patterns.
     *
     * @param minOccurrences The minimum number of co-occurrences
     * @return Specialty pair statistics
     */
    @Query("SELECT s1.id as specialty1Id, s1.name as specialty1Name, " +
           "s2.id as specialty2Id, s2.name as specialty2Name, COUNT(p) as count " +
           "FROM SpecialtyPrescription sp1 JOIN sp1.prescription p JOIN sp1.specialty s1, " +
           "SpecialtyPrescription sp2 JOIN sp2.prescription p2 JOIN sp2.specialty s2 " +
           "WHERE p.id = p2.id AND s1.id < s2.id " +
           "GROUP BY s1.id, s1.name, s2.id, s2.name " +
           "HAVING COUNT(p) >= :minOccurrences " +
           "ORDER BY count DESC")
    List<Object[]> findFrequentSpecialtyPairs(@Param("minOccurrences") Long minOccurrences);
    
    /**
     * Find most common treatments prescribed by a specialty.
     *
     * @param specialtyId The specialty ID
     * @return Treatment frequency statistics for the specialty
     */
    @Query("SELECT t.id as treatmentId, t.name as treatmentName, COUNT(pt) as count " +
           "FROM SpecialtyPrescription sp JOIN sp.prescription p JOIN p.treatments pt " +
           "JOIN pt.treatment t WHERE sp.specialty.id = :specialtyId " +
           "GROUP BY t.id, t.name ORDER BY count DESC")
    List<Object[]> findMostCommonTreatmentsBySpecialty(@Param("specialtyId") Long specialtyId);
    
    /**
     * Find most common diagnoses addressed by a specialty's prescriptions.
     *
     * @param specialtyId The specialty ID
     * @return Diagnosis frequency statistics for the specialty
     */
    @Query("SELECT d.id as diagnosisId, d.name as diagnosisName, COUNT(dmi) as count " +
           "FROM SpecialtyPrescription sp JOIN sp.prescription p " +
           "JOIN p.medicalRecordItems mrip JOIN mrip.medicalRecordItem mri " +
           "JOIN mri.diagnoses dmi JOIN dmi.diagnosis d " +
           "WHERE sp.specialty.id = :specialtyId " +
           "GROUP BY d.id, d.name ORDER BY count DESC")
    List<Object[]> findMostCommonDiagnosesBySpecialty(@Param("specialtyId") Long specialtyId);
    
    /**
     * Find specialties with cross-specialty prescriptions.
     * This identifies cases where a doctor from one specialty prescribes for another specialty.
     *
     * @return Cross-specialty prescription statistics
     */
    @Query("SELECT ds.specialty.id as doctorSpecialtyId, ds.specialty.name as doctorSpecialtyName, " +
           "sp.specialty.id as prescriptionSpecialtyId, sp.specialty.name as prescriptionSpecialtyName, " +
           "COUNT(sp) as count FROM SpecialtyPrescription sp JOIN sp.prescription p " +
           "JOIN p.doctor d JOIN d.specialties ds " +
           "WHERE sp.specialty.id != ds.specialty.id " +
           "GROUP BY ds.specialty.id, ds.specialty.name, sp.specialty.id, sp.specialty.name " +
           "ORDER BY count DESC")
    List<Object[]> findCrossSpecialtyPrescriptions();
    
    /**
     * Find prescription volume by patient demographics for a specialty.
     * This example looks at age groups.
     *
     * @param specialtyId The specialty ID
     * @return Age group distribution for the specialty's prescriptions
     */
    @Query("SELECT " +
           "CASE " +
           "WHEN FUNCTION('TIMESTAMPDIFF', YEAR, pt.birthDate, CURRENT_DATE) < 18 THEN 'Under 18' " +
           "WHEN FUNCTION('TIMESTAMPDIFF', YEAR, pt.birthDate, CURRENT_DATE) BETWEEN 18 AND 30 THEN '18-30' " +
           "WHEN FUNCTION('TIMESTAMPDIFF', YEAR, pt.birthDate, CURRENT_DATE) BETWEEN 31 AND 50 THEN '31-50' " +
           "WHEN FUNCTION('TIMESTAMPDIFF', YEAR, pt.birthDate, CURRENT_DATE) BETWEEN 51 AND 65 THEN '51-65' " +
           "ELSE 'Over 65' END as ageGroup, " +
           "COUNT(sp) as count FROM SpecialtyPrescription sp " +
           "JOIN sp.prescription p JOIN p.patient pt " +
           "WHERE sp.specialty.id = :specialtyId " +
           "GROUP BY ageGroup ORDER BY ageGroup")
    List<Object[]> findPrescriptionPatternsByAgeGroup(@Param("specialtyId") Long specialtyId);
}