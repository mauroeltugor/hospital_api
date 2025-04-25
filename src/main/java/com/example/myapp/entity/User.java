package com.example.myapp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "cc", unique = true)
    @NotBlank(message = "CC is required")
    private String cc;
    
    @Column(name = "first_name")
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @Column(name = "last_name")
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "email", unique = true)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Column(name = "password_hash")
    @NotBlank(message = "Password is required")
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_activated")
    private Boolean isActivated;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    @JsonManagedReference
    private Address address;
    
    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private Set<UserNotification> userNotifications = new HashSet<>();
    
    // Enum for User Types
    public enum UserType {
        PATIENT, DOCTOR, ADMIN
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActivated == null) {
            isActivated = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
