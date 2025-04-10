package com.example.hospital_api.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "prescriptions")
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String notes;
    private LocalDateTime issuedAt;
    
    @ManyToOne
    @JoinColumn(name = "medical_record_item_id")
    private MedicalRecordItem medicalRecordItem;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    
    public MedicalRecordItem getMedicalRecordItem() { return medicalRecordItem; }
    public void setMedicalRecordItem(MedicalRecordItem medicalRecordItem) { this.medicalRecordItem = medicalRecordItem; }
}
