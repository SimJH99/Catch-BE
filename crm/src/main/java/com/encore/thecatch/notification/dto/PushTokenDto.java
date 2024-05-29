package com.encore.thecatch.notification.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PushTokenDto {
    private String pushToken;
}
