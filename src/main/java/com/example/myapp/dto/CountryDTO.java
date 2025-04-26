package com.example.myapp.dto;

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
public class CountryDTO {

    private Integer id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 10)
    private String code;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
