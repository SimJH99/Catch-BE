package com.encore.thecatch.user.dto.response;

import com.encore.thecatch.user.domain.Grade;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileDto {
    private String name;
    private Grade grade;
}
