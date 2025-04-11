package com.example.hospital_api.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "specialties")
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    
    @OneToMany(mappedBy = "specialty", cascade = CascadeType.ALL)
    private List<DoctorSpecialty> doctors = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DoctorSpecialty> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<DoctorSpecialty> doctors) {
        this.doctors = doctors;
    }

    
}
