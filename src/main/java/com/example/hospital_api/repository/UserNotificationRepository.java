package com.example.hospital_api.repository;

import com.example.hospital_api.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {
    List<UserNotification> findByUserId(int userId);
    List<UserNotification> findByNotificationId(int notificationId);
    List<UserNotification> findByIsRead(Boolean isRead);
    List<UserNotification> findByIsDeleted(Boolean isDeleted);
    List<UserNotification> findByDeliveredAtBetween(LocalDateTime start, LocalDateTime end);
}