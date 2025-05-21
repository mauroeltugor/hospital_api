package com.example.myapp.repository.admin;

import com.example.myapp.entity.Notification;
import com.example.myapp.entity.Notification.NotificationType;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing system notifications.
 * Provides queries for notification tracking and analysis.
 */
@Repository
public interface NotificationRepository extends BaseRepository<Notification, Long> {

    /**
     * Find notifications by type.
     *
     * @param type The notification type
     * @return List of notifications of the specified type
     */
    List<Notification> findByType(NotificationType type);
    
    /**
     * Find notifications with title or message containing text.
     *
     * @param searchText The text to search for
     * @return List of notifications with matching content
     */
    @Query("SELECT n FROM Notification n WHERE " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(n.message) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Notification> findByContentContainingIgnoreCase(@Param("searchText") String searchText);
    
    /**
     * Find notifications created in a date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of notifications created in the date range
     */
    List<Notification> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find scheduled notifications.
     *
     * @param currentDateTime The current date and time
     * @return List of notifications scheduled for the future
     */
    @Query("SELECT n FROM Notification n WHERE n.scheduledAt > :currentDateTime")
    List<Notification> findScheduledNotifications(@Param("currentDateTime") LocalDateTime currentDateTime);
    
    /**
     * Find notifications ready for delivery.
     * These are scheduled notifications that should be sent now.
     *
     * @param currentDateTime The current date and time
     * @return List of notifications ready for delivery
     */
    @Query("SELECT n FROM Notification n WHERE n.scheduledAt <= :currentDateTime " +
           "AND n.scheduledAt IS NOT NULL")
    List<Notification> findNotificationsReadyForDelivery(@Param("currentDateTime") LocalDateTime currentDateTime);
    
    /**
     * Find notifications with pagination and filtering.
     *
     * @param type The notification type (optional)
     * @param searchText The search text (optional)
     * @param pageable Pagination information
     * @return Page of filtered notifications
     */
    @Query("SELECT n FROM Notification n WHERE " +
           "(:type IS NULL OR n.type = :type) AND " +
           "(:searchText IS NULL OR " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(n.message) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    Page<Notification> findWithFilters(
            @Param("type") NotificationType type,
            @Param("searchText") String searchText,
            Pageable pageable);
    
    /**
     * Find notifications for a specific user.
     *
     * @param userId The user's ID
     * @return List of notifications for the user
     */
    @Query("SELECT n FROM Notification n JOIN n.userNotifications un " +
           "WHERE un.user.id = :userId")
    List<Notification> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find unread notifications for a user.
     *
     * @param userId The user's ID
     * @return List of unread notifications for the user
     */
    @Query("SELECT n FROM Notification n JOIN n.userNotifications un " +
           "WHERE un.user.id = :userId AND un.isRead = false AND un.isDeleted = false")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);
    
    /**
     * Count notifications by type.
     *
     * @return Notification counts grouped by type
     */
    @Query("SELECT n.type as type, COUNT(n) as count " +
           "FROM Notification n GROUP BY n.type ORDER BY count DESC")
    List<Object[]> countByType();
    
    /**
     * Count notifications by creation date periods.
     *
     * @param interval The time interval ('MONTH', 'QUARTER', 'YEAR')
     * @return Notification counts grouped by creation period
     */
    @Query("SELECT FUNCTION('DATE_FORMAT', n.createdAt, " +
           "CASE :interval " +
           "WHEN 'MONTH' THEN '%Y-%m' " +
           "WHEN 'QUARTER' THEN '%Y-Q%q' " +
           "WHEN 'YEAR' THEN '%Y' " +
           "ELSE '%Y-%m' END) as timePeriod, " +
           "COUNT(n) as count FROM Notification n " +
           "GROUP BY timePeriod ORDER BY timePeriod")
    List<Object[]> countNotificationsByCreationPeriod(@Param("interval") String interval);
    
    /**
     * Find most recent notification by type for each user.
     *
     * @param type The notification type
     * @return List of most recent notifications
     */
    @Query("SELECT un.user.id as userId, MAX(n.id) as notificationId " +
           "FROM Notification n JOIN n.userNotifications un " +
           "WHERE n.type = :type " +
           "GROUP BY un.user.id")
    List<Object[]> findMostRecentNotificationByTypeForUsers(@Param("type") NotificationType type);
    
    /**
     * Count read vs unread notifications.
     *
     * @return Counts of read and unread notifications
     */
    @Query("SELECT un.isRead as isRead, COUNT(un) as count " +
           "FROM UserNotification un WHERE un.isDeleted = false " +
           "GROUP BY un.isRead")
    List<Object[]> countReadVsUnreadNotifications();
    
    /**
     * Find similar notifications based on title or message content.
     * This can be useful for identifying notification patterns or duplicates.
     *
     * @param notificationId The reference notification ID
     * @return List of similar notifications
     */
    @Query("SELECT n2 FROM Notification n1, Notification n2 " +
           "WHERE n1.id = :notificationId AND n2.id != n1.id AND " +
           "((LOWER(n2.title) LIKE LOWER(CONCAT('%', n1.title, '%'))) OR " +
           "(LOWER(n2.message) LIKE LOWER(CONCAT('%', n1.message, '%'))))")
    List<Notification> findSimilarNotifications(@Param("notificationId") Long notificationId);
    
    /**
     * Find similar notifications by keywords.
     * Provides an alternative approach to finding similar content.
     *
     * @param keywords Keywords to search for
     * @return List of notifications containing the keywords
     */
    @Query("SELECT n FROM Notification n WHERE " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :keywords, '%')) OR " +
           "LOWER(n.message) LIKE LOWER(CONCAT('%', :keywords, '%'))")
    List<Notification> findByKeywords(@Param("keywords") String keywords);
}