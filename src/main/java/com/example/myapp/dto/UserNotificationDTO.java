package com.example.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationDTO {

    private Integer id;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer notificationId;

    private Boolean isRead;
    private LocalDateTime readAt;
    private Boolean isDeleted;
    private LocalDateTime deliveredAt;
}
