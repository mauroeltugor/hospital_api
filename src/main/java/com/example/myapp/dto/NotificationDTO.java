package com.example.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {

    private Integer id;

    @NotBlank
    @Size(max = 150)
    private String title;

    @NotBlank
    private String message;

    @NotNull
    private String type; // Enum: system | alert | reminder | appointment | result

    @Future
    private LocalDateTime scheduledAt;

    private LocalDateTime createdAt;
}
