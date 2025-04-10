package com.example.hospital_api.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "doctor_schedule_dates")
public class DoctorScheduleDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private DoctorSchedule schedule;
    
    private Date date;
    
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;
    
    private String notes;
    
    @OneToMany(mappedBy = "scheduleDate", cascade = CascadeType.ALL)
    private List<Appointment> appointments = new ArrayList<>();

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public DoctorSchedule getSchedule() { return schedule; }
    public void setSchedule(DoctorSchedule schedule) { this.schedule = schedule; }
    
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    
    public ScheduleStatus getStatus() { return status; }
    public void setStatus(ScheduleStatus status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }

    // ScheduleStatus enum
    public enum ScheduleStatus {
        AVAILABLE,
        UNAVAILABLE,
        VACATION,
        HOLIDAY
    }
}


