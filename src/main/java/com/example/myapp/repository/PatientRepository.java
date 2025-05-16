package com.example.myapp.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

import com.example.myapp.entity.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
     boolean existsByUsername(String username);

     @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.username) LIKE LOWER(CONCAT('%', :filter, '%')) OR " +
           "CAST(p.userId AS string) LIKE CONCAT('%', :filter, '%')")
    Page<Patient> findByUsernameContainingOrUserIdContaining(@Param("filter") String filter, Pageable pageable);
}
