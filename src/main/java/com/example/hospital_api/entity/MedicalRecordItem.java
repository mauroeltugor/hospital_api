package com.example.hospital_api.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "medical_record_items")
public class MedicalRecordItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "medical_record_id")
    private MedicalRecord medicalRecord;
    
    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;
    
    private LocalDateTime entryDate;
    private String notes;
    
    @OneToMany(mappedBy = "medicalRecordItem", cascade = CascadeType.ALL)
    private List<Diagnosis> diagnoses = new ArrayList<>();
    
    @OneToMany(mappedBy = "medicalRecordItem", cascade = CascadeType.ALL)
    private List<Treatment> treatments = new ArrayList<>();
    
    @OneToMany(mappedBy = "medicalRecordItem", cascade = CascadeType.ALL)
    private List<Prescription> prescriptions = new ArrayList<>();

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public MedicalRecord getMedicalRecord() { return medicalRecord; }
    public void setMedicalRecord(MedicalRecord medicalRecord) { this.medicalRecord = medicalRecord; }
    
    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }
    
    public LocalDateTime getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDateTime entryDate) { this.entryDate = entryDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public List<Diagnosis> getDiagnoses() { return diagnoses; }
    public void setDiagnoses(List<Diagnosis> diagnoses) { this.diagnoses = diagnoses; }
    
    public List<Treatment> getTreatments() { return treatments; }
    public void setTreatments(List<Treatment> treatments) { this.treatments = treatments; }
    
    public List<Prescription> getPrescriptions() { return prescriptions; }
    public void setPrescriptions(List<Prescription> prescriptions) { this.prescriptions = prescriptions; }
}
