package com.example.myapp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "allergies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Allergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "allergy", cascade = CascadeType.ALL)
    @JsonManagedReference("allergy-patient")
    @Builder.Default
    private Set<PatientAllergy> patients = new HashSet<>();

    public enum Severity {
        MILD, MODERATE, SEVERE, LIFE_THREATENING
    }
}
