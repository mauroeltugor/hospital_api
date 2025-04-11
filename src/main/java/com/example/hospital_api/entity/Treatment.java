package com.example.hospital_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "treatments")
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String name;
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "medical_record_item_id")
    private MedicalRecordItem medicalRecordItem;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public MedicalRecordItem getMedicalRecordItem() { return medicalRecordItem; }
    public void setMedicalRecordItem(MedicalRecordItem medicalRecordItem) { this.medicalRecordItem = medicalRecordItem; }
}
