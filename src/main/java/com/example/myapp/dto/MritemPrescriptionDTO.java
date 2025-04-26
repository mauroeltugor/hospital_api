package com.example.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MritemPrescriptionDTO {

    private Integer id;

    @NotNull
    private Integer medicalRecordItemId;

    @NotNull
    private Integer prescriptionId;
}
