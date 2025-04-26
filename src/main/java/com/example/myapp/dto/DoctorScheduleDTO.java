package com.example.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorScheduleDTO {

    private Integer id;

    @NotNull
    private Integer doctorId;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private LocalTime breakStart;
    private LocalTime breakEnd;

    @NotNull
    @Min(1)
    private Integer maxAppointments;

    @NotNull
    private String workDay; // Puedes usar un Enum si lo prefieres (por ejemplo: MONDAY, TUESDAY, etc.)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
