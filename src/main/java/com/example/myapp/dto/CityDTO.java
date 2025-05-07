package com.example.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityDTO {

    private Integer id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private Integer countryId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
