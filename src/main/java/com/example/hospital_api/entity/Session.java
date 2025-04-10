package com.example.hospital_api.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    
    private Date sessionDate;
    private int effectiveness;
    private String observations;
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<MedicalRecordItem> medicalRecordItems = new ArrayList<>();

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    
    public Date getSessionDate() { return sessionDate; }
    public void setSessionDate(Date sessionDate) { this.sessionDate = sessionDate; }
    
    public int getEffectiveness() { return effectiveness; }
    public void setEffectiveness(int effectiveness) { this.effectiveness = effectiveness; }
    
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    
    public List<MedicalRecordItem> getMedicalRecordItems() { return medicalRecordItems; }
    public void setMedicalRecordItems(List<MedicalRecordItem> medicalRecordItems) { this.medicalRecordItems = medicalRecordItems; }
}
