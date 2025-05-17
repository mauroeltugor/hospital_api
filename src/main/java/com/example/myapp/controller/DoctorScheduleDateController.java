package com.example.myapp.controller;

import com.example.myapp.entity.DoctorScheduleDate;
import com.example.myapp.services.DoctorScheduleDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctor-schedule-dates")
public class DoctorScheduleDateController {

    @Autowired
    private DoctorScheduleDateService doctorScheduleDateService;

    @PostMapping
    public ResponseEntity<DoctorScheduleDate> createDoctorScheduleDate(@RequestBody DoctorScheduleDate doctorScheduleDate) {
        return new ResponseEntity<>(doctorScheduleDateService.create(doctorScheduleDate), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DoctorScheduleDate>> getAllDoctorScheduleDates() {
        return new ResponseEntity<>(doctorScheduleDateService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorScheduleDate> getDoctorScheduleDateById(@PathVariable Long id) {
        Optional<DoctorScheduleDate> scheduleDate = Optional.of(doctorScheduleDateService.findById(id));
        return scheduleDate.map(s -> new ResponseEntity<>(s, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorScheduleDate> updateDoctorScheduleDate(@PathVariable Long id, @RequestBody DoctorScheduleDate doctorScheduleDate) {
        try {
            return new ResponseEntity<>(doctorScheduleDateService.update(id, doctorScheduleDate), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctorScheduleDate(@PathVariable Long id) {
        doctorScheduleDateService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}


