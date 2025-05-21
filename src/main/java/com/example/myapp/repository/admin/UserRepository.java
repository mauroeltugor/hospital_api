package com.example.myapp.repository.admin;

import com.example.myapp.entity.User;
import com.example.myapp.entity.User.UserType;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing user entities.
 * Provides queries for user management and analysis.
 */
@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    /**
     * Find a user by CC (identification number).
     *
     * @param cc The identification number
     * @return The user if found
     */
    Optional<User> findByCc(String cc);
    
    /**
     * Find a user by username.
     *
     * @param username The username
     * @return The user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find users by user type.
     *
     * @param userType The user type
     * @return List of users with the specified type
     */
    List<User> findByUserType(UserType userType);
    
    /**
     * Find users by activation status.
     *
     * @param isActivated The activation status
     * @return List of users with the specified activation status
     */
    List<User> findByIsActivated(Boolean isActivated);
    
    /**
     * Find users by first name or last name containing text.
     *
     * @param searchText The text to search for
     * @return List of users with matching names
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("searchText") String searchText);
    
    /**
     * Find users by address ID.
     *
     * @param addressId The address ID
     * @return List of users at the specified address
     */
    List<User> findByAddressId(Long addressId);
    
    /**
     * Find users with pagination and filtering.
     *
     * @param userType The user type (optional)
     * @param isActivated The activation status (optional)
     * @param searchText The search text for name (optional)
     * @param pageable Pagination information
     * @return Page of filtered users
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:userType IS NULL OR u.userType = :userType) AND " +
           "(:isActivated IS NULL OR u.isActivated = :isActivated) AND " +
           "(:searchText IS NULL OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(u.cc) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    Page<User> findWithFilters(
            @Param("userType") UserType userType,
            @Param("isActivated") Boolean isActivated,
            @Param("searchText") String searchText,
            Pageable pageable);
    
    /**
     * Find recently created users.
     *
     * @param days The number of days to look back
     * @return List of recently created users
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :startDate")
    List<User> findRecentlyCreatedUsers(@Param("startDate") LocalDateTime startDate);
    
    /**
     * Find users by last login date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of users who logged in during the date range
     */
    List<User> findByLastLoginBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find users who haven't logged in since a specific date.
     *
     * @param sinceDate The cutoff date
     * @return List of inactive users
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin < :sinceDate OR u.lastLogin IS NULL")
    List<User> findInactiveUsersSince(@Param("sinceDate") LocalDateTime sinceDate);
    
    /**
     * Count users by type.
     *
     * @return User counts grouped by type
     */
    @Query("SELECT u.userType as userType, COUNT(u) as count " +
           "FROM User u GROUP BY u.userType ORDER BY count DESC")
    List<Object[]> countByUserType();
    
    /**
     * Count users by activation status.
     *
     * @return User counts grouped by activation status
     */
    @Query("SELECT u.isActivated as isActivated, COUNT(u) as count " +
           "FROM User u GROUP BY u.isActivated")
    List<Object[]> countByActivationStatus();
    
    /**
     * Find users by notification type.
     * This finds users who have received a specific type of notification.
     *
     * @param notificationType The notification type
     * @return List of users who received the notification type
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.notifications un " +
           "JOIN un.notification n WHERE n.type = :notificationType")
    List<User> findByNotificationType(@Param("notificationType") String notificationType);
    
    /**
     * Find users with unread notifications.
     *
     * @return List of users with unread notifications
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.notifications un " +
           "WHERE un.isRead = false AND un.isDeleted = false")
    List<User> findWithUnreadNotifications();
    
    /**
     * Count users by creation date periods.
     * Useful for analyzing user growth over time.
     *
     * @param interval The time interval ('MONTH', 'QUARTER', 'YEAR')
     * @return User counts grouped by creation period
     */
    @Query("SELECT FUNCTION('DATE_FORMAT', u.createdAt, " +
           "CASE :interval " +
           "WHEN 'MONTH' THEN '%Y-%m' " +
           "WHEN 'QUARTER' THEN '%Y-Q%q' " +
           "WHEN 'YEAR' THEN '%Y' " +
           "ELSE '%Y-%m' END) as timePeriod, " +
           "COUNT(u) as count FROM User u " +
           "GROUP BY timePeriod ORDER BY timePeriod")
    List<Object[]> countUsersByCreationPeriod(@Param("interval") String interval);
    
    /**
     * Validate user credentials.
     * Note: In a real application, this would be handled by a security framework,
     * but this method illustrates the concept.
     *
     * @param username The username
     * @param passwordHash The password hash
     * @return The user if credentials are valid
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND " +
           "u.passwordHash = :passwordHash AND u.isActivated = true")
    Optional<User> validateCredentials(
            @Param("username") String username,
            @Param("passwordHash") String passwordHash);
}