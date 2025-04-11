package com.example.hospital_api.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
@Entity
@Table(name = "allergies")
public class Allergy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String name;
    
    @Enumerated(EnumType.STRING)
    private AllergySeverity severity;
    
    private String notes;
    
    @OneToMany(mappedBy = "allergy", cascade = CascadeType.ALL)
    private List<PatientAllergy> patients = new ArrayList<>();

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public AllergySeverity getSeverity() { return severity; }
    public void setSeverity(AllergySeverity severity) { this.severity = severity; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public List<PatientAllergy> getPatients() { return patients; }
    public void setPatients(List<PatientAllergy> patients) { this.patients = patients; }

    // AllergySeverity enum
    public enum AllergySeverity {
        MILD,
        MODERATE,
        SEVERE,
        LIFE_THREATENING
    }
}



