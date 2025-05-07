package com.example.myapp.dto;

import com.example.myapp.enums.Status;
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
public class SpecialtyDTO {

    private Integer id;

    @NotBlank
    @Size(max = 100)
    private String name;

    private Status status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
