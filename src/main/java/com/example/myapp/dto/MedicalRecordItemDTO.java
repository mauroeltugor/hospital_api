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
public class MedicalRecordItemDTO {

    private Integer id;

    @NotNull
    private Integer medicalRecordId;

    @NotNull
    private Integer doctorId;

    @PastOrPresent
    private LocalDate entryDate;

    @Size(max = 500)
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
