package com.encore.thecatch.user.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
public class UserUpdateDto {
    private String userNotice;
    private String grade;
}
