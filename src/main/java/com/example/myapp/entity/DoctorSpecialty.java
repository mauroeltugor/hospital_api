package com.example.myapp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.Date;

@Entity
@Table(name = "doctor_specialties")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSpecialty {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @JsonBackReference
    private Doctor doctor;
    
    @ManyToOne
    @JoinColumn(name = "specialty_id")
    @JsonBackReference
    private Specialty specialty;
    
    @Column(name = "certification_date")
    @Temporal(TemporalType.DATE)
    private Date certificationDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private ExperienceLevel experienceLevel;
    
    public enum ExperienceLevel {
        JUNIOR, INTERMEDIATE, SENIOR, EXPERT
    }
}
