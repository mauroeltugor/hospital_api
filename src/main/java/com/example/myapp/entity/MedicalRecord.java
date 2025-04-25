package com.example.myapp.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "medical_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @OneToOne
    @JoinColumn(name = "patient_id")
    @JsonBackReference
    private Patient patient;
    
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<MedicalRecordItem> medicalRecordItems = new HashSet<>();
}