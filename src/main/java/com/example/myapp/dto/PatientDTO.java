package com.example.myapp.dto;

import com.example.myapp.enums.Gender;
import com.example.myapp.enums.BloodType;
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
public class PatientDTO {

    private Integer userId;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    private Gender gender;  // Enum Gender

    private BloodType bloodType;  // Enum BloodType

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
