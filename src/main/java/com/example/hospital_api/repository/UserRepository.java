package com.example.hospital_api.repository;

import com.example.hospital_api.entity.*;
import com.example.hospital_api.entity.User.UserType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByEmail(String email);
    List<User> findByUserType(UserType userType);
    List<User> findByLastLoginBetween(LocalDateTime start, LocalDateTime end);
}
