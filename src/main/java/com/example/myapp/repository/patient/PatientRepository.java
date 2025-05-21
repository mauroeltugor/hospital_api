package com.example.myapp.repository.patient;

import com.example.myapp.entity.Patient;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Patient entities.
 * Extends BaseRepository to inherit common functionality.
 */
@Repository
public interface PatientRepository extends BaseRepository<Patient, Long> {
    
    /**
     * Find a patient by their identification number (CC).
     *
     * @param cc The identification number
     * @return The patient, if found
     */
    Optional<Patient> findByCc(String cc);
    
    /**
     * Find patients by their blood type.
     *
     * @param bloodType The blood type to search for
     * @return List of patients with the specified blood type
     */
    List<Patient> findByBloodType(Patient.BloodType bloodType);
    
    /**
     * Find patients by their first name (case insensitive partial match).
     *
     * @param firstName The first name to search for
     * @return List of matching patients
     */
    List<Patient> findByFirstNameContainingIgnoreCase(String firstName);
    
    /**
     * Find patients by their last name (case insensitive partial match).
     *
     * @param lastName The last name to search for
     * @return List of matching patients
     */
    List<Patient> findByLastNameContainingIgnoreCase(String lastName);
}