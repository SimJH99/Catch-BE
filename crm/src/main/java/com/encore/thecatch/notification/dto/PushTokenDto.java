package com.encore.thecatch.notification.dto;

import lombok.Data;

@Data
public class PushTokenDto {
    private String employeeNumber;
    private String pushToken;
}
