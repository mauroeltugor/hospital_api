package com.example.hospital_api.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String title;
    private String message;
    private String type;
    private LocalDateTime scheduledAt;
    
    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL)
    private List<UserNotification> userNotifications = new ArrayList<>();

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    
    public List<UserNotification> getUserNotifications() { return userNotifications; }
    public void setUserNotifications(List<UserNotification> userNotifications) { this.userNotifications = userNotifications; }
}


