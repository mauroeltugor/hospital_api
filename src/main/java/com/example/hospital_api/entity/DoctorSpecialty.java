package com.example.hospital_api.entity;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "doctor_specialties")
public class DoctorSpecialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    
    @ManyToOne
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;
    
    private Date certificationDate;

    public enum ExperienceLevel {
        JUNIOR,
        INTERMEDIATE,
        SENIOR,
        EXPERT
    }

    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    
    public Specialty getSpecialty() { return specialty; }
    public void setSpecialty(Specialty specialty) { this.specialty = specialty; }
    
    public Date getCertificationDate() { return certificationDate; }
    public void setCertificationDate(Date certificationDate) { this.certificationDate = certificationDate; }
    
    public ExperienceLevel getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(ExperienceLevel experienceLevel) { this.experienceLevel = experienceLevel; }
}

