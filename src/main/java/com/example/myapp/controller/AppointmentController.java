package com.example.myapp.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.myapp.dto.AppointmentDTO;
import com.example.myapp.entity.Appointment;
import com.example.myapp.services.AppointmentService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PutMapping("/cancel/{id}")
    public ResponseEntity<String> cancelAppointment(@PathVariable Integer id, @RequestBody AppointmentDTO request) {
        try {
            appointmentService.cancelAppointment(id, request);
            return ResponseEntity.ok("Cita cancelada exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cancelar la cita: " + e.getMessage());
        }
    }

    @GetMapping("/day")
    public ResponseEntity<List<Appointment>> getAppointmentsByDate(@RequestParam String date, @RequestParam(required = false) Long doctorId, @RequestParam(required = false) Long specialtyId) {
        try {
            LocalDate queryDate = LocalDate.parse(date);
            List<Appointment> appointments = appointmentService.getAppointmentsByDate(queryDate, doctorId, specialtyId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<Appointment>> getAppointmentStatistics(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<Appointment> statistics = appointmentService.getAppointmentStatistics(start, end);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
