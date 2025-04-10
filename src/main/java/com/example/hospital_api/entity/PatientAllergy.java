package com.example.hospital_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "patient_allergies")
public class PatientAllergy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "allergy_id")
    private Allergy allergy;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    
    public Allergy getAllergy() { return allergy; }
    public void setAllergy(Allergy allergy) { this.allergy = allergy; }
}
