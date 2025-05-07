package com.example.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionDTO {

    private Integer id;

    @NotNull
    private Integer treatmentId;

    @NotNull
    private Integer patientId;

    @PastOrPresent
    private LocalDate sessionDate;

    private Integer effectiveness;

    @Size(max = 500)
    private String observations;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
