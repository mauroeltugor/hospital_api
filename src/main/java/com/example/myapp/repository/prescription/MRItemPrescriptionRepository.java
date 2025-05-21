package com.example.myapp.repository.prescription;

import com.example.myapp.entity.MRItemPrescription;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing medical record item to prescription relationships.
 * Provides queries for analyzing connections between medical records and prescriptions.
 */
@Repository
public interface MRItemPrescriptionRepository extends BaseRepository<MRItemPrescription, Long> {

    /**
     * Find relationship by medical record item ID and prescription ID.
     *
     * @param medicalRecordItemId The medical record item ID
     * @param prescriptionId The prescription ID
     * @return The relationship if found
     */
    Optional<MRItemPrescription> findByMedicalRecordItemIdAndPrescriptionId(
            Long medicalRecordItemId, 
            Long prescriptionId);
    
    /**
     * Find relationships by medical record item ID.
     *
     * @param medicalRecordItemId The medical record item ID
     * @return List of relationships for the medical record item
     */
    List<MRItemPrescription> findByMedicalRecordItemId(Long medicalRecordItemId);
    
    /**
     * Find relationships by prescription ID.
     *
     * @param prescriptionId The prescription ID
     * @return List of relationships for the prescription
     */
    List<MRItemPrescription> findByPrescriptionId(Long prescriptionId);
    
    /**
     * Find relationships for a specific patient.
     *
     * @param patientId The patient's ID
     * @return List of relationships for the patient
     */
    @Query("SELECT mrip FROM MRItemPrescription mrip JOIN mrip.medicalRecordItem mri " +
           "JOIN mri.medicalRecord mr WHERE mr.patient.id = :patientId")
    List<MRItemPrescription> findByPatientId(@Param("patientId") Long patientId);
    
    /**
     * Find relationships created by a specific doctor, through medical record items.
     *
     * @param doctorId The doctor's ID
     * @return List of relationships created by the doctor
     */
    @Query("SELECT mrip FROM MRItemPrescription mrip JOIN mrip.medicalRecordItem mri " +
           "WHERE mri.doctor.id = :doctorId")
    List<MRItemPrescription> findByMedicalRecordItemDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Find relationships created by a specific doctor, through prescriptions.
     *
     * @param doctorId The doctor's ID
     * @return List of relationships created by the doctor
     */
    @Query("SELECT mrip FROM MRItemPrescription mrip JOIN mrip.prescription p " +
           "WHERE p.doctor.id = :doctorId")
    List<MRItemPrescription> findByPrescriptionDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Find relationships created in a date range, through medical record items.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of relationships created in the date range
     */
    @Query("SELECT mrip FROM MRItemPrescription mrip JOIN mrip.medicalRecordItem mri " +
           "WHERE mri.entryDate BETWEEN :startDate AND :endDate")
    List<MRItemPrescription> findByMedicalRecordItemDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find relationships created in a date range, through prescriptions.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of relationships created in the date range
     */
    @Query("SELECT mrip FROM MRItemPrescription mrip JOIN mrip.prescription p " +
           "WHERE p.issuedAt BETWEEN :startDate AND :endDate")
    List<MRItemPrescription> findByPrescriptionDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find relationships for a specific diagnosis.
     *
     * @param diagnosisId The diagnosis ID
     * @return List of relationships for the diagnosis
     */
    @Query("SELECT mrip FROM MRItemPrescription mrip JOIN mrip.medicalRecordItem mri " +
           "JOIN mri.diagnoses dmi WHERE dmi.diagnosis.id = :diagnosisId")
    List<MRItemPrescription> findByDiagnosisId(@Param("diagnosisId") Long diagnosisId);
    
    /**
     * Find relationships for a specific treatment.
     *
     * @param treatmentId The treatment ID
     * @return List of relationships for the treatment
     */
    @Query("SELECT mrip FROM MRItemPrescription mrip JOIN mrip.prescription p " +
           "JOIN p.treatments pt WHERE pt.treatment.id = :treatmentId")
    List<MRItemPrescription> findByTreatmentId(@Param("treatmentId") Long treatmentId);
    
    /**
     * Find relationships for a specific specialty, through diagnoses.
     *
     * @param specialtyId The specialty ID
     * @return List of relationships for the specialty
     */
    @Query("SELECT mrip FROM MRItemPrescription mrip JOIN mrip.medicalRecordItem mri " +
           "JOIN mri.diagnoses dmi JOIN dmi.diagnosis d WHERE d.specialty.id = :specialtyId")
    List<MRItemPrescription> findByDiagnosisSpecialtyId(@Param("specialtyId") Long specialtyId);
    
    /**
     * Find relationships for a specific specialty, through prescriptions.
     *
     * @param specialtyId The specialty ID
     * @return List of relationships for the specialty
     */
    @Query("SELECT mrip FROM MRItemPrescription mrip JOIN mrip.prescription p " +
           "JOIN p.specialties ps WHERE ps.specialty.id = :specialtyId")
    List<MRItemPrescription> findByPrescriptionSpecialtyId(@Param("specialtyId") Long specialtyId);
    
    /**
     * Count relationships by diagnosis.
     *
     * @return Relationship counts grouped by diagnosis
     */
    @Query("SELECT dmi.diagnosis.id as diagnosisId, dmi.diagnosis.name as diagnosisName, " +
           "COUNT(mrip) as count FROM MRItemPrescription mrip " +
           "JOIN mrip.medicalRecordItem mri JOIN mri.diagnoses dmi " +
           "GROUP BY dmi.diagnosis.id, dmi.diagnosis.name ORDER BY count DESC")
    List<Object[]> countByDiagnosis();
    
    /**
     * Count relationships by treatment.
     *
     * @return Relationship counts grouped by treatment
     */
    @Query("SELECT pt.treatment.id as treatmentId, pt.treatment.name as treatmentName, " +
           "COUNT(mrip) as count FROM MRItemPrescription mrip " +
           "JOIN mrip.prescription p JOIN p.treatments pt " +
           "GROUP BY pt.treatment.id, pt.treatment.name ORDER BY count DESC")
    List<Object[]> countByTreatment();
    
    /**
     * Count relationships by doctor.
     *
     * @return Relationship counts grouped by doctor
     */
    @Query("SELECT p.doctor.id as doctorId, p.doctor.firstName as firstName, " +
           "p.doctor.lastName as lastName, COUNT(mrip) as count " +
           "FROM MRItemPrescription mrip JOIN mrip.prescription p " +
           "GROUP BY p.doctor.id, p.doctor.firstName, p.doctor.lastName " +
           "ORDER BY count DESC")
    List<Object[]> countByDoctor();
    
    /**
     * Find common diagnosis-treatment combinations.
     * Useful for identifying standard treatment protocols.
     *
     * @param minOccurrences The minimum number of occurrences
     * @return Diagnosis-treatment combination statistics
     */
    @Query("SELECT dmi.diagnosis.id as diagnosisId, dmi.diagnosis.name as diagnosisName, " +
           "pt.treatment.id as treatmentId, pt.treatment.name as treatmentName, " +
           "COUNT(mrip) as count FROM MRItemPrescription mrip " +
           "JOIN mrip.medicalRecordItem mri JOIN mri.diagnoses dmi " +
           "JOIN mrip.prescription p JOIN p.treatments pt " +
           "GROUP BY dmi.diagnosis.id, dmi.diagnosis.name, pt.treatment.id, pt.treatment.name " +
           "HAVING COUNT(mrip) >= :minOccurrences " +
           "ORDER BY count DESC")
    List<Object[]> findCommonDiagnosisTreatmentCombinations(@Param("minOccurrences") Long minOccurrences);
    
    /**
     * Find treatment patterns for a specific diagnosis.
     *
     * @param diagnosisId The diagnosis ID
     * @return Treatment pattern statistics for the diagnosis
     */
    @Query("SELECT pt.treatment.id as treatmentId, pt.treatment.name as treatmentName, " +
           "COUNT(mrip) as count FROM MRItemPrescription mrip " +
           "JOIN mrip.medicalRecordItem mri JOIN mri.diagnoses dmi " +
           "JOIN mrip.prescription p JOIN p.treatments pt " +
           "WHERE dmi.diagnosis.id = :diagnosisId " +
           "GROUP BY pt.treatment.id, pt.treatment.name " +
           "ORDER BY count DESC")
    List<Object[]> findTreatmentPatternsForDiagnosis(@Param("diagnosisId") Long diagnosisId);
    
    /**
     * Find diagnosis patterns for a specific treatment.
     *
     * @param treatmentId The treatment ID
     * @return Diagnosis pattern statistics for the treatment
     */
    @Query("SELECT dmi.diagnosis.id as diagnosisId, dmi.diagnosis.name as diagnosisName, " +
           "COUNT(mrip) as count FROM MRItemPrescription mrip " +
           "JOIN mrip.medicalRecordItem mri JOIN mri.diagnoses dmi " +
           "JOIN mrip.prescription p JOIN p.treatments pt " +
           "WHERE pt.treatment.id = :treatmentId " +
           "GROUP BY dmi.diagnosis.id, dmi.diagnosis.name " +
           "ORDER BY count DESC")
    List<Object[]> findDiagnosisPatternsForTreatment(@Param("treatmentId") Long treatmentId);
}