package com.example.myapp.dto;

import com.example.myapp.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllergyDTO {

    private Integer id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    private Severity severity;  // Enum Severity

    @Size(max = 500)
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
