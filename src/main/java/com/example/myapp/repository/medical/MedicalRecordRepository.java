package com.example.myapp.repository.medical;

import com.example.myapp.entity.MedicalRecord;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing medical records.
 * Provides queries for accessing and analyzing patient medical histories.
 */
@Repository
public interface MedicalRecordRepository extends BaseRepository<MedicalRecord, Long> {

    /**
     * Find a medical record by patient ID.
     * Since there's a one-to-one relationship, this should return a single record.
     *
     * @param patientId The patient's ID
     * @return The medical record if found
     */
    Optional<MedicalRecord> findByPatientId(Long patientId);
    
    /**
     * Find a medical record with all items eager loaded.
     * Useful for displaying a complete medical history.
     *
     * @param id The medical record ID
     * @return The medical record with all items if found
     */
    @Query("SELECT mr FROM MedicalRecord mr LEFT JOIN FETCH mr.items WHERE mr.id = :id")
    Optional<MedicalRecord> findByIdWithItems(@Param("id") Long id);
    
    /**
     * Find a medical record by patient ID with all items eager loaded.
     *
     * @param patientId The patient's ID
     * @return The medical record with all items if found
     */
    @Query("SELECT mr FROM MedicalRecord mr LEFT JOIN FETCH mr.items WHERE mr.patient.id = :patientId")
    Optional<MedicalRecord> findByPatientIdWithItems(@Param("patientId") Long patientId);
    
    /**
     * Find medical records for patients with a specific diagnosis.
     *
     * @param diagnosisId The diagnosis ID
     * @return List of medical records with the specified diagnosis
     */
    @Query("SELECT DISTINCT mr FROM MedicalRecord mr JOIN mr.items mri " +
           "JOIN mri.diagnoses dmi WHERE dmi.diagnosis.id = :diagnosisId")
    List<MedicalRecord> findByDiagnosisId(@Param("diagnosisId") Long diagnosisId);
    
    /**
     * Find medical records for patients treated by a specific doctor.
     *
     * @param doctorId The doctor's ID
     * @return List of medical records for patients treated by the doctor
     */
    @Query("SELECT DISTINCT mr FROM MedicalRecord mr JOIN mr.items mri WHERE mri.doctor.id = :doctorId")
    List<MedicalRecord> findByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Count the number of entries (items) in a medical record.
     *
     * @param medicalRecordId The medical record ID
     * @return The count of items in the medical record
     */
    @Query("SELECT COUNT(mri) FROM MedicalRecordItem mri WHERE mri.medicalRecord.id = :medicalRecordId")
    Long countItemsInMedicalRecord(@Param("medicalRecordId") Long medicalRecordId);
    
    /**
     * Find medical records with prescriptions for a specific treatment.
     *
     * @param treatmentId The treatment ID
     * @return List of medical records with the specified treatment
     */
    @Query("SELECT DISTINCT mr FROM MedicalRecord mr JOIN mr.items mri " +
           "JOIN mri.prescriptions mrip JOIN mrip.prescription p " +
           "JOIN p.treatments pt WHERE pt.treatment.id = :treatmentId")
    List<MedicalRecord> findByTreatmentId(@Param("treatmentId") Long treatmentId);
    
    /**
     * Find medical records with entries in a specific specialty area.
     *
     * @param specialtyId The specialty ID
     * @return List of medical records with entries in the specialty
     */
    @Query("SELECT DISTINCT mr FROM MedicalRecord mr JOIN mr.items mri " +
           "JOIN mri.diagnoses dmi JOIN dmi.diagnosis d " +
           "WHERE d.specialty.id = :specialtyId")
    List<MedicalRecord> findBySpecialtyId(@Param("specialtyId") Long specialtyId);
}