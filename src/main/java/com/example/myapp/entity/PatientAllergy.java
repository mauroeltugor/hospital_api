package com.example.myapp.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "patient_allergies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientAllergy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonBackReference
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "allergy_id")
    @JsonBackReference
    private Allergy allergy;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private Severity severity;
    
    public enum Severity {
        MILD, MODERATE, SEVERE, LIFE_THREATENING
    }
}