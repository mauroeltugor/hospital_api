package com.example.myapp.controller;

import com.example.myapp.entity.DoctorSchedule;
import com.example.myapp.services.DoctorScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
public class DoctorScheduleController {

    private final DoctorScheduleService scheduleService;

    @PostMapping("/{doctorId}")
    public ResponseEntity<DoctorSchedule> crearHorario(
            @PathVariable Long doctorId,
            @Valid @RequestBody DoctorSchedule schedule) {
        return ResponseEntity.ok(scheduleService.createSchedule(doctorId, schedule));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorSchedule> actualizarHorario(
            @PathVariable Long id,
            @Valid @RequestBody DoctorSchedule schedule) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, schedule));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorSchedule> obtenerHorario(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getSchedule(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHorario(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<DoctorSchedule>> listarHorarios() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }
}

