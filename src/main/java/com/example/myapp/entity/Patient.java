package com.example.myapp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patients")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends User {
    
    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    
    @Column(name = "gender")
    private String gender;
    
    @Column(name = "blood_type")
    private String bloodType;
    
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Appointment> appointments = new HashSet<>();
    
    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonManagedReference
    private MedicalRecord medicalRecord;
    
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<PatientAllergy> patientAllergies = new HashSet<>();
}
