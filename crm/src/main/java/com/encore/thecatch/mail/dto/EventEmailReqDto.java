package com.encore.thecatch.mail.dto;

import lombok.Data;

import java.util.List;

@Data
public class EventEmailReqDto {
    private List<Long> userIds;
}
