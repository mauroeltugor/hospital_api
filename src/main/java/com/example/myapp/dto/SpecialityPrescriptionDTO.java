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
public class SpecialityPrescriptionDTO {

    private Integer id;

    @NotNull
    private Integer prescriptionId;

    @NotNull
    private Integer specialtyId;
}
