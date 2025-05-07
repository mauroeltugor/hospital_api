package com.example.myapp.repository;

import com.example.myapp.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Integer> {
}
