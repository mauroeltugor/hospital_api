package com.example.myapp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "specialties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Specialty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "specialty", cascade = CascadeType.ALL)
    @JsonManagedReference("specialty-doctor")
    @Builder.Default
    private Set<DoctorSpecialty> doctors = new HashSet<>();

    @OneToMany(mappedBy = "specialty", cascade = CascadeType.ALL)
    @JsonManagedReference("specialty-diagnosis")
    @Builder.Default
    private Set<Diagnosis> diagnoses = new HashSet<>();

    @OneToMany(mappedBy = "specialty", cascade = CascadeType.ALL)
    @JsonManagedReference("specialty-treatment")
    @Builder.Default
    private Set<Treatment> treatments = new HashSet<>();

    @OneToMany(mappedBy = "specialty", cascade = CascadeType.ALL)
    @JsonManagedReference("specialty-prescription")
    @Builder.Default
    private Set<SpecialtyPrescription> prescriptions = new HashSet<>();
}
