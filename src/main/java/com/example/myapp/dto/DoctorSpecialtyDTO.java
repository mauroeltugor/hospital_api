package com.example.myapp.dto;

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
public class DoctorSpecialtyDTO {

    private Integer id;

    @NotNull
    private Integer doctorId;

    @NotNull
    private Integer specialtyId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
