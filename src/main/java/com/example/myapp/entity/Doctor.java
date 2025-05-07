package com.example.myapp.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "doctors")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Doctor extends User {

    @Column(name = "license_number", unique = true, nullable = false)
    private String licenseNumber;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    @JsonManagedReference("doctor-schedule")
    @Builder.Default
    private Set<DoctorSchedule> schedules = new HashSet<>();

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    @JsonManagedReference("doctor-medical-record-item")
    @Builder.Default
    private Set<MedicalRecordItem> medicalRecordItems = new HashSet<>();

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    @JsonManagedReference("doctor-prescription")
    @Builder.Default
    private Set<Prescription> prescriptions = new HashSet<>();

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    @JsonManagedReference("doctor-specialty")
    @Builder.Default
    private Set<DoctorSpecialty> specialties = new HashSet<>();

}