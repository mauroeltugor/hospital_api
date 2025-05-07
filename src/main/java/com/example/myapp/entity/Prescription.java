package com.example.myapp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    @JsonBackReference("appointment-prescription")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonBackReference("patient-prescription")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonBackReference("doctor-prescription")
    private Doctor doctor;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL)
    @JsonManagedReference("prescription-treatment")
    @Builder.Default
    private Set<PrescriptionTreatment> treatments = new HashSet<>();

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL)
    @JsonManagedReference("prescription-mritem")
    @Builder.Default
    private Set<MRItemPrescription> medicalRecordItems = new HashSet<>();

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL)
    @JsonManagedReference("prescription-specialty")
    @Builder.Default
    private Set<SpecialtyPrescription> specialties = new HashSet<>();
}