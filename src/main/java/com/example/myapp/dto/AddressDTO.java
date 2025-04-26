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
public class AddressDTO {

    private Integer id;

    @NotBlank
    @Size(max = 255)
    private String streetLine1;

    @Size(max = 255)
    private String streetLine2;

    @NotBlank
    @Size(max = 20)
    private String postalCode;

    @NotNull
    private Integer cityId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
