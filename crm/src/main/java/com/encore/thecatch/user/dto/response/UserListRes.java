package com.encore.thecatch.user.dto.response;

import com.encore.thecatch.user.domain.Gender;
import com.encore.thecatch.user.domain.Grade;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
public class UserListRes {
    private Long id;
    private String name;
    private String email;
    private LocalDate birthDate;
    private String phoneNumber;
    private Gender gender;
    private Grade grade;

    @QueryProjection
    public UserListRes(Long id,
                       String name,
                       String email,
                       LocalDate birthDate,
                       String phoneNumber,
                       Gender gender,
                       Grade grade) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.grade = grade;
    }
}
