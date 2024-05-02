package com.encore.thecatch.notification.dto;

import lombok.Data;

import java.util.List;

@Data
public class EventNotificationReqDto {
    private List<Long> userIds;
}
