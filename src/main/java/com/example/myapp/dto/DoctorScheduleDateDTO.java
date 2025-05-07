package com.example.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorScheduleDateDTO {

    private Integer id;

    @NotNull
    private Integer scheduleId;

    @NotNull
    private LocalDate date;

    @NotNull
    private Boolean status; // activo o inactivo

    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
