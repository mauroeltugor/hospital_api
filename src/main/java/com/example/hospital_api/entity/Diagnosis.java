package com.example.hospital_api.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "diagnoses")
public class Diagnosis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String name;
    private String description;
    private LocalDateTime issuedAt;
    
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
    
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    
    public MedicalRecordItem getMedicalRecordItem() { return medicalRecordItem; }
    public void setMedicalRecordItem(MedicalRecordItem medicalRecordItem) { this.medicalRecordItem = medicalRecordItem; }
}
