package com.example.myapp.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.Date;

@Entity
@Table(name = "doctor_schedule_dates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleDate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "doctor_schedule_id")
    @JsonBackReference
    private DoctorSchedule doctorSchedule;
    
    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    public enum Status {
        ACTIVE, CANCELLED, COMPLETED
    }
}
