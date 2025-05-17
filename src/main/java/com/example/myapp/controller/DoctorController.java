package com.example.myapp.controller;

import com.example.myapp.dto.DoctorScheduleDTO;
import com.example.myapp.dto.DoctorSpecialtyDTO;
import com.example.myapp.entity.Doctor;
import com.example.myapp.entity.DoctorSchedule;
import com.example.myapp.entity.DoctorSchedule.WorkDay;
import com.example.myapp.entity.DoctorSpecialty;
import com.example.myapp.services.DoctorService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/doctores")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public List<Doctor> getAll() {
        return doctorService.getAll();
    }

    @GetMapping("/{id}")
    public Doctor getById(@PathVariable Long id) {
        return doctorService.getById(id);
                
    }

    @PostMapping
    public Doctor create(@RequestBody Doctor doctor) {
        return doctorService.create(doctor);
    }

    @PutMapping("/{id}")
    public Doctor updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        return doctorService.updateDoctor(id, doctor);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        doctorService.delete(id);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerDoctor(@RequestBody DoctorSpecialtyDTO request) {
        Doctor doctor = Doctor.builder()
                .userId(request.getUserId())
                .licenseNumber(request.getLicenseNumber())
                // otros campos heredados de User si es necesario
                .build();

        String result = doctorService.registerDoctor(doctor, request.getSpecialtyIds());

        if (result.contains("exitosamente")) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
    }

    @PutMapping("/{doctorId}/schedule")
    public ResponseEntity<?> updateDoctorSchedule(
            @PathVariable Long doctorId,
            @RequestBody List<DoctorScheduleDTO> requestList) {

        List<DoctorSchedule> schedules = requestList.stream().map(req -> DoctorSchedule.builder()
                .workDay(WorkDay.valueOf(req.getWorkDay()))
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .breakStart(req.getBreakStart())
                .breakEnd(req.getBreakEnd())
                .maxAppointments(req.getMaxAppointments())
                .createdAt(LocalDateTime.now())
                .build()
        ).toList();

        List<DoctorSchedule> result = doctorService.updateSchedules(doctorId, schedules);

        return ResponseEntity.ok(result);
    }
}

