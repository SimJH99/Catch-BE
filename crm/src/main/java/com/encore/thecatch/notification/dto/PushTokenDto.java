package com.encore.thecatch.notification.dto;

import lombok.Data;

@Data
public class PushTokenDto {
    private String email;
    private String pushToken;
}
