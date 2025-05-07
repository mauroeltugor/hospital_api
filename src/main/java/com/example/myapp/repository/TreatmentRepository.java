package com.example.myapp.repository;

import com.example.myapp.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Integer> {
}
