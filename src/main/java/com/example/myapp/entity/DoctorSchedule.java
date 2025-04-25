package com.example.myapp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "doctor_schedules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @JsonBackReference
    private Doctor doctor;
    
    @Column(name = "start_time")
    private LocalTime startTime;
    
    @Column(name = "end_time")
    private LocalTime endTime;
    
    @Column(name = "break_start")
    private LocalTime breakStart;
    
    @Column(name = "break_end")
    private LocalTime breakEnd;
    
    @Column(name = "max_appointments")
    private Integer maxAppointments;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "work_day")
    private WorkDay workDay;
    
    @OneToMany(mappedBy = "doctorSchedule", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<DoctorScheduleDate> doctorScheduleDates = new HashSet<>();
    
    public enum WorkDay {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }
}