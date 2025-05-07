package com.example.myapp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patients")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Patient extends User {

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type")
    private BloodType bloodType;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonManagedReference("patient-medical-record")
    private MedicalRecord medicalRecord;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonManagedReference("patient-appointment")
    @Builder.Default
    private Set<Appointment> appointments = new HashSet<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonManagedReference("patient-prescription")
    @Builder.Default
    private Set<Prescription> prescriptions = new HashSet<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonManagedReference("patient-session")
    @Builder.Default
    private Set<Session> sessions = new HashSet<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonManagedReference("patient-allergies")
    @Builder.Default
    private Set<PatientAllergy> allergies = new HashSet<>();

    public enum Gender {
        MALE, FEMALE, OTHER, NOT_SPECIFIED
    }

    public enum BloodType {
        A_POSITIVE, A_NEGATIVE,
        B_POSITIVE, B_NEGATIVE,
        AB_POSITIVE, AB_NEGATIVE,
        O_POSITIVE, O_NEGATIVE,
        UNKNOWN
    }
}