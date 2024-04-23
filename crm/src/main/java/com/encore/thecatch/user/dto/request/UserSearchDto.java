package com.encore.thecatch.user.dto.request;

import com.encore.thecatch.user.domain.Gender;
import com.encore.thecatch.user.domain.Grade;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UserSearchDto {
    private String name;
    private String email;
    private LocalDate birthDate;
    private String phoneNumber;
    private Gender gender;
    private Grade grade;
}
