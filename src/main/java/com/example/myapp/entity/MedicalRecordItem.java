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
@Table(name = "medical_record_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    @JsonBackReference("medical-record-item")
    private MedicalRecord medicalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonBackReference("doctor-medical-record-item")
    private Doctor doctor;

    @Column(name = "entry_date", nullable = false)
    private LocalDateTime entryDate;

    @Column(columnDefinition = "TEXT", name = "notes")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "medicalRecordItem", cascade = CascadeType.ALL)
    @JsonManagedReference("mri-diagnosis")
    @Builder.Default
    private Set<DiagnosisMedItem> diagnoses = new HashSet<>();

    @OneToMany(mappedBy = "medicalRecordItem", cascade = CascadeType.ALL)
    @JsonManagedReference("mri-prescription")
    @Builder.Default
    private Set<MRItemPrescription> prescriptions = new HashSet<>();
}