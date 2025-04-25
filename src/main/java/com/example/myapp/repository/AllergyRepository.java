package com.example.myapp.repository;

import com.example.myapp.entity.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, Integer> {
    List<Allergy> findByNameContaining(String name);
    List<Allergy> findByNotesContaining(String notes);
}
