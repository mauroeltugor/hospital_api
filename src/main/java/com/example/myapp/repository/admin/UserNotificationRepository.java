package com.example.myapp.repository.admin;

import com.example.myapp.entity.UserNotification;
import com.example.myapp.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
   
    // Buscar notificaciones por usuario
    List<UserNotification> findByUserIdAndIsDeletedFalseOrderByNotification_CreatedAtDesc(Long userId);
   
    // Contar notificaciones no leídas
    @Query("SELECT COUNT(un) FROM UserNotification un WHERE un.user.id = :userId AND un.isRead = false AND un.isDeleted = false")
    int countUnreadByUserId(@Param("userId") Long userId);
   
    // Marcar todas las notificaciones como leídas
    @Modifying
    @Transactional
    @Query("UPDATE UserNotification un SET un.isRead = true, un.readAt = :readTime WHERE un.user.id = :userId AND un.isRead = false AND un.isDeleted = false")
    int markAllAsReadByUserId(@Param("userId") Long userId, @Param("readTime") LocalDateTime readTime);
   
    // Marcar notificación como leída
    @Modifying
    @Transactional
    @Query("UPDATE UserNotification un SET un.isRead = true, un.readAt = :readTime WHERE un.id = :notificationId AND un.isRead = false")
    int markAsRead(@Param("notificationId") Long notificationId, @Param("readTime") LocalDateTime readTime);
   
    // Marcar notificación como eliminada
    @Modifying
    @Transactional
    @Query("UPDATE UserNotification un SET un.isDeleted = true WHERE un.id = :notificationId AND un.user.id = :userId")
    int markAsDeleted(@Param("notificationId") Long notificationId, @Param("userId") Long userId);
   
    // Buscar notificaciones por tipo
    @Query("SELECT un FROM UserNotification un WHERE un.user.id = :userId AND un.notification.type = :type AND un.isDeleted = false ORDER BY un.notification.createdAt DESC")
    List<UserNotification> findByNotificationType(@Param("userId") Long userId, @Param("type") Notification.NotificationType type);
   
    // Actualizar estado de entrega
    @Modifying
    @Transactional
    @Query("UPDATE UserNotification un SET un.deliveredAt = :deliveredAt WHERE un.id = :id AND un.deliveredAt IS NULL")
    int updateDeliveryStatus(@Param("id") Long id, @Param("deliveredAt") LocalDateTime deliveredAt);
}