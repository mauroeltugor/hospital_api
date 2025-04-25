package com.example.myapp.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_record_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "medical_record_id")
    @JsonBackReference
    private MedicalRecord medicalRecord;
    
    @Column(name = "entry_date")
    private LocalDateTime entryDate;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
