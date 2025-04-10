package com.example.hospital_api.entity;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "doctor_schedules")
public class DoctorSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    
    private Time startTime;
    private Time endTime;
    private Time breakStart;
    private Time breakEnd;
    private int maxAppointments;
    
    @Enumerated(EnumType.STRING)
    private WorkDay workDay;
    
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<DoctorScheduleDate> scheduleDates = new ArrayList<>();

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    
    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }
    
    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }
    
    public Time getBreakStart() { return breakStart; }
    public void setBreakStart(Time breakStart) { this.breakStart = breakStart; }
    
    public Time getBreakEnd() { return breakEnd; }
    public void setBreakEnd(Time breakEnd) { this.breakEnd = breakEnd; }
    
    public int getMaxAppointments() { return maxAppointments; }
    public void setMaxAppointments(int maxAppointments) { this.maxAppointments = maxAppointments; }
    
    public WorkDay getWorkDay() { return workDay; }
    public void setWorkDay(WorkDay workDay) { this.workDay = workDay; }
    
    public List<DoctorScheduleDate> getScheduleDates() { return scheduleDates; }
    public void setScheduleDates(List<DoctorScheduleDate> scheduleDates) { this.scheduleDates = scheduleDates; }
    
    // WorkDay enum
    public enum WorkDay {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }
}



