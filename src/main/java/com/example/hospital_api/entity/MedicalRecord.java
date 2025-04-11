package com.example.hospital_api.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @OneToMany(mappedBy = "medicalRecord", cascade = CascadeType.ALL)
    private List<MedicalRecordItem> items = new ArrayList<>();

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    
    public List<MedicalRecordItem> getItems() { return items; }
    public void setItems(List<MedicalRecordItem> items) { this.items = items; }
}