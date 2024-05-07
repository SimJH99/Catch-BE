package com.encore.thecatch.notification.dto;

import com.encore.thecatch.notification.domain.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResDto {
    private Long id;
    private String notificationTitle;
    private String notificationContent;
    private LocalDateTime createdTime;

    public static NotificationResDto toNotificationResDto(Notification notification){
        NotificationResDto notificationResDto = NotificationResDto.builder()
                .id(notification.getId())
                .notificationTitle(notification.getNotificationTitle())
                .notificationContent(notification.getNotificationContent())
                .createdTime(notification.getCreatedTime())
                .build();
        return notificationResDto;
    }
}
