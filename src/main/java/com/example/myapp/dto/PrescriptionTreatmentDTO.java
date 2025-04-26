package com.example.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionTreatmentDTO {

    private Integer id;

    @NotNull
    private Integer prescriptionId;

    @NotNull
    private Integer treatmentId;

    @Size(max = 500)
    private String dosage;

    @Size(max = 500)
    private String notes;
}
