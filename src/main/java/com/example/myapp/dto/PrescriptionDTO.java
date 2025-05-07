package com.example.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionDTO {

    private Integer id;

    @NotNull
    private Integer appointmentId;

    @NotNull
    private Integer patientId;

    @NotNull
    private Integer doctorId;

    private String notes;

    @PastOrPresent
    private LocalDateTime issuedAt;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
