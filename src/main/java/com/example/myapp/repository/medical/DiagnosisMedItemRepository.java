package com.example.myapp.repository.medical;

import com.example.myapp.entity.DiagnosisMedItem;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing diagnosis-medical record item relationships.
 * Provides queries for analyzing diagnosis assignments and patterns.
 */
@Repository
public interface DiagnosisMedItemRepository extends BaseRepository<DiagnosisMedItem, Long> {

    /**
     * Find diagnosis assignments by diagnosis ID.
     *
     * @param diagnosisId The diagnosis ID
     * @return List of diagnosis assignments
     */
    List<DiagnosisMedItem> findByDiagnosisId(Long diagnosisId);
    
    /**
     * Find diagnosis assignments by medical record item ID.
     *
     * @param medicalRecordItemId The medical record item ID
     * @return List of diagnosis assignments
     */
    List<DiagnosisMedItem> findByMedicalRecordItemId(Long medicalRecordItemId);
    
    /**
     * Find diagnosis assignments for a specific patient.
     *
     * @param patientId The patient's ID
     * @return List of diagnosis assignments for the patient
     */
    @Query("SELECT dmi FROM DiagnosisMedItem dmi JOIN dmi.medicalRecordItem mri " +
           "JOIN mri.medicalRecord mr WHERE mr.patient.id = :patientId")
    List<DiagnosisMedItem> findByPatientId(@Param("patientId") Long patientId);
    
    /**
     * Find diagnosis assignments made by a specific doctor.
     *
     * @param doctorId The doctor's ID
     * @return List of diagnosis assignments made by the doctor
     */
    @Query("SELECT dmi FROM DiagnosisMedItem dmi JOIN dmi.medicalRecordItem mri " +
           "WHERE mri.doctor.id = :doctorId")
    List<DiagnosisMedItem> findByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Find diagnosis assignments in a specific date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of diagnosis assignments in the date range
     */
    @Query("SELECT dmi FROM DiagnosisMedItem dmi JOIN dmi.medicalRecordItem mri " +
           "WHERE mri.entryDate BETWEEN :startDate AND :endDate")
    List<DiagnosisMedItem> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count diagnosis assignments by diagnosis.
     *
     * @return Diagnosis assignment counts
     */
    @Query("SELECT dmi.diagnosis.id as diagnosisId, dmi.diagnosis.name as diagnosisName, " +
           "COUNT(dmi) as count FROM DiagnosisMedItem dmi " +
           "GROUP BY dmi.diagnosis.id, dmi.diagnosis.name ORDER BY count DESC")
    List<Object[]> countByDiagnosis();
    
    /**
     * Count diagnosis assignments by doctor.
     *
     * @return Doctor diagnosis assignment counts
     */
    @Query("SELECT mri.doctor.id as doctorId, mri.doctor.firstName as firstName, " +
           "mri.doctor.lastName as lastName, COUNT(dmi) as count " +
           "FROM DiagnosisMedItem dmi JOIN dmi.medicalRecordItem mri " +
           "GROUP BY mri.doctor.id, mri.doctor.firstName, mri.doctor.lastName " +
           "ORDER BY count DESC")
    List<Object[]> countByDoctor();
    
    /**
     * Find diagnosis assignments for a specific diagnosis made by a specific doctor.
     *
     * @param diagnosisId The diagnosis ID
     * @param doctorId The doctor's ID
     * @return List of matching diagnosis assignments
     */
    @Query("SELECT dmi FROM DiagnosisMedItem dmi JOIN dmi.medicalRecordItem mri " +
           "WHERE dmi.diagnosis.id = :diagnosisId AND mri.doctor.id = :doctorId")
    List<DiagnosisMedItem> findByDiagnosisIdAndDoctorId(
            @Param("diagnosisId") Long diagnosisId,
            @Param("doctorId") Long doctorId);
    
    /**
     * Find diagnosis assignments for a specific patient and diagnosis.
     *
     * @param patientId The patient's ID
     * @param diagnosisId The diagnosis ID
     * @return List of matching diagnosis assignments
     */
    @Query("SELECT dmi FROM DiagnosisMedItem dmi JOIN dmi.medicalRecordItem mri " +
           "JOIN mri.medicalRecord mr " +
           "WHERE mr.patient.id = :patientId AND dmi.diagnosis.id = :diagnosisId")
    List<DiagnosisMedItem> findByPatientIdAndDiagnosisId(
            @Param("patientId") Long patientId,
            @Param("diagnosisId") Long diagnosisId);
    
    /**
     * Find diagnosis assignments with related prescriptions.
     *
     * @return List of diagnosis assignments with prescriptions
     */
    @Query("SELECT DISTINCT dmi FROM DiagnosisMedItem dmi JOIN dmi.medicalRecordItem mri " +
           "JOIN mri.prescriptions mrip")
    List<DiagnosisMedItem> findWithPrescriptions();
    
    /**
     * Find diagnosis assignments with a specific treatment.
     *
     * @param treatmentId The treatment ID
     * @return List of diagnosis assignments related to the treatment
     */
    @Query("SELECT DISTINCT dmi FROM DiagnosisMedItem dmi JOIN dmi.medicalRecordItem mri " +
           "JOIN mri.prescriptions mrip JOIN mrip.prescription p " +
           "JOIN p.treatments pt WHERE pt.treatment.id = :treatmentId")
    List<DiagnosisMedItem> findByTreatmentId(@Param("treatmentId") Long treatmentId);
    
    /**
     * Find most frequent co-occurring diagnoses.
     * Identifies diagnoses that are often assigned together.
     *
     * @param minOccurrences The minimum number of co-occurrences
     * @return Diagnosis pair statistics
     */
    @Query("SELECT d1.id as diagnosis1Id, d1.name as diagnosis1Name, " +
           "d2.id as diagnosis2Id, d2.name as diagnosis2Name, COUNT(mri) as count " +
           "FROM DiagnosisMedItem dmi1 JOIN dmi1.medicalRecordItem mri JOIN dmi1.diagnosis d1, " +
           "DiagnosisMedItem dmi2 JOIN dmi2.medicalRecordItem mri2 JOIN dmi2.diagnosis d2 " +
           "WHERE mri.id = mri2.id AND d1.id < d2.id " +
           "GROUP BY d1.id, d1.name, d2.id, d2.name " +
           "HAVING COUNT(mri) >= :minOccurrences " +
           "ORDER BY count DESC")
    List<Object[]> findFrequentDiagnosisPairs(@Param("minOccurrences") Long minOccurrences);
    
    /**
     * Find diagnosis patterns over time for a specific patient.
     * Useful for tracking disease progression or recurrence.
     *
     * @param patientId The patient's ID
     * @return Chronological diagnosis history
     */
    @Query("SELECT dmi.diagnosis.id as diagnosisId, dmi.diagnosis.name as diagnosisName, " +
           "mri.entryDate as entryDate FROM DiagnosisMedItem dmi " +
           "JOIN dmi.medicalRecordItem mri JOIN mri.medicalRecord mr " +
           "WHERE mr.patient.id = :patientId " +
           "ORDER BY mri.entryDate")
    List<Object[]> findDiagnosisPatternForPatient(@Param("patientId") Long patientId);
    
    /**
     * Find diagnosis assignments by specialty.
     *
     * @param specialtyId The specialty ID
     * @return List of diagnosis assignments in the specialty
     */
    @Query("SELECT dmi FROM DiagnosisMedItem dmi JOIN dmi.diagnosis d " +
           "WHERE d.specialty.id = :specialtyId")
    List<DiagnosisMedItem> findBySpecialtyId(@Param("specialtyId") Long specialtyId);
    
    /**
     * Count diagnosis assignments by patient age group.
     * Useful for epidemiological studies.
     *
     * @param minAge The minimum age in years
     * @param maxAge The maximum age in years
     * @return Diagnosis counts by age group
     */
    @Query("SELECT dmi.diagnosis.id as diagnosisId, dmi.diagnosis.name as diagnosisName, " +
           "COUNT(dmi) as count FROM DiagnosisMedItem dmi " +
           "JOIN dmi.medicalRecordItem mri JOIN mri.medicalRecord mr JOIN mr.patient p " +
           "WHERE FUNCTION('TIMESTAMPDIFF', YEAR, p.birthDate, CURRENT_DATE) BETWEEN :minAge AND :maxAge " +
           "GROUP BY dmi.diagnosis.id, dmi.diagnosis.name ORDER BY count DESC")
    List<Object[]> countByAgeGroup(
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge);
}