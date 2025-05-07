package com.example.myapp.dto;

import com.example.myapp.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDTO {

    private Integer id;

    @NotNull
    private Integer scheduleId;

    @NotNull
    private Integer patientId;

    private Integer effectiveness;

    @NotNull
    private Integer specialtyId;

    @NotNull
    private Status status;  // Enum Status

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
