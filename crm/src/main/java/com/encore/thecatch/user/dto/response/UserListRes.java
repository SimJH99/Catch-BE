package com.encore.thecatch.user.dto.response;

import com.encore.thecatch.user.domain.Gender;
import com.encore.thecatch.user.domain.Grade;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class UserListRes {
    private Long id;
    private String name;
    private Gender gender;
    private String email;
    private Grade grade;

    @QueryProjection
    public UserListRes(Long id,
                       String name,
                       Gender gender,
                       String email,
                       Grade grade) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.grade = grade;
    }
}
