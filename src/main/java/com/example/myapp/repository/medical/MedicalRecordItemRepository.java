package com.example.myapp.repository.medical;

import com.example.myapp.entity.MedicalRecordItem;
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
 * Repository for managing medical record items (entries).
 * Provides queries for detailed medical record analysis and retrieval.
 */
@Repository
public interface MedicalRecordItemRepository extends BaseRepository<MedicalRecordItem, Long> {

    /**
     * Find a medical record item with all related diagnoses and prescriptions.
     *
     * @param id The medical record item ID
     * @return The medical record item with details if found
     */
    @Query("SELECT mri FROM MedicalRecordItem mri " +
           "LEFT JOIN FETCH mri.diagnoses d " +
           "LEFT JOIN FETCH mri.prescriptions p " +
           "WHERE mri.id = :id")
    Optional<MedicalRecordItem> findByIdWithDetails(@Param("id") Long id);
    
    /**
     * Find all items in a specific medical record.
     *
     * @param medicalRecordId The medical record ID
     * @return List of items in the medical record
     */
    List<MedicalRecordItem> findByMedicalRecordId(Long medicalRecordId);
    
    /**
     * Find all items created by a specific doctor.
     *
     * @param doctorId The doctor's ID
     * @return List of items created by the doctor
     */
    List<MedicalRecordItem> findByDoctorId(Long doctorId);
    
    /**
     * Find items created in a specific date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of items created in the date range
     */
    List<MedicalRecordItem> findByEntryDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find items for a specific patient by medical record.
     *
     * @param patientId The patient's ID
     * @return List of items for the patient
     */
    @Query("SELECT mri FROM MedicalRecordItem mri WHERE mri.medicalRecord.patient.id = :patientId")
    List<MedicalRecordItem> findByPatientId(@Param("patientId") Long patientId);
    
    /**
     * Find items for a patient in a date range.
     *
     * @param patientId The patient's ID
     * @param startDate The start date
     * @param endDate The end date
     * @return List of items for the patient in the date range
     */
    @Query("SELECT mri FROM MedicalRecordItem mri " +
           "WHERE mri.medicalRecord.patient.id = :patientId " +
           "AND mri.entryDate BETWEEN :startDate AND :endDate")
    List<MedicalRecordItem> findByPatientIdAndDateRange(
            @Param("patientId") Long patientId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find items with a specific diagnosis.
     *
     * @param diagnosisId The diagnosis ID
     * @return List of items with the diagnosis
     */
    @Query("SELECT mri FROM MedicalRecordItem mri JOIN mri.diagnoses dmi " +
           "WHERE dmi.diagnosis.id = :diagnosisId")
    List<MedicalRecordItem> findByDiagnosisId(@Param("diagnosisId") Long diagnosisId);
    
    /**
     * Find items with a specific prescription.
     *
     * @param prescriptionId The prescription ID
     * @return List of items with the prescription
     */
    @Query("SELECT mri FROM MedicalRecordItem mri JOIN mri.prescriptions mrip " +
           "WHERE mrip.prescription.id = :prescriptionId")
    List<MedicalRecordItem> findByPrescriptionId(@Param("prescriptionId") Long prescriptionId);
    
    /**
     * Find items containing specific text in notes.
     *
     * @param searchText The text to search for
     * @return List of items with matching notes
     */
    @Query("SELECT mri FROM MedicalRecordItem mri WHERE LOWER(mri.notes) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<MedicalRecordItem> findByNotesContainingIgnoreCase(@Param("searchText") String searchText);
    
    /**
     * Find recent entries for a patient with pagination.
     *
     * @param patientId The patient's ID
     * @param pageable Pagination information
     * @return Page of recent items for the patient
     */
    @Query("SELECT mri FROM MedicalRecordItem mri " +
           "WHERE mri.medicalRecord.patient.id = :patientId " +
           "ORDER BY mri.entryDate DESC")
    Page<MedicalRecordItem> findRecentItemsByPatientId(
            @Param("patientId") Long patientId,
            Pageable pageable);
    
    /**
     * Count items by doctor.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Item counts grouped by doctor
     */
    @Query("SELECT mri.doctor.id as doctorId, mri.doctor.firstName as firstName, " +
           "mri.doctor.lastName as lastName, COUNT(mri) as count " +
           "FROM MedicalRecordItem mri " +
           "WHERE mri.entryDate BETWEEN :startDate AND :endDate " +
           "GROUP BY mri.doctor.id, mri.doctor.firstName, mri.doctor.lastName " +
           "ORDER BY count DESC")
    List<Object[]> countItemsByDoctor(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count items by diagnosis.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Item counts grouped by diagnosis
     */
    @Query("SELECT dmi.diagnosis.id as diagnosisId, dmi.diagnosis.name as diagnosisName, " +
           "COUNT(dmi) as count FROM MedicalRecordItem mri JOIN mri.diagnoses dmi " +
           "WHERE mri.entryDate BETWEEN :startDate AND :endDate " +
           "GROUP BY dmi.diagnosis.id, dmi.diagnosis.name " +
           "ORDER BY count DESC")
    List<Object[]> countItemsByDiagnosis(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find items for patients with a specific condition (diagnosis) treated by a specific doctor.
     *
     * @param diagnosisId The diagnosis ID
     * @param doctorId The doctor's ID
     * @return List of matching items
     */
    @Query("SELECT mri FROM MedicalRecordItem mri JOIN mri.diagnoses dmi " +
           "WHERE dmi.diagnosis.id = :diagnosisId AND mri.doctor.id = :doctorId")
    List<MedicalRecordItem> findByDiagnosisIdAndDoctorId(
            @Param("diagnosisId") Long diagnosisId,
            @Param("doctorId") Long doctorId);
}