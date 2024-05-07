package com.encore.thecatch.user.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PublishUserDto {
    private List<Long> userIds;
}
