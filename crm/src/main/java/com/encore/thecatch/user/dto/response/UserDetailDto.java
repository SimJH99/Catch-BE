package com.encore.thecatch.user.dto.response;

import com.encore.thecatch.user.domain.Gender;
import com.encore.thecatch.user.domain.Grade;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserDetailDto {
    private String name;
    private String email;
    private LocalDate birthDate;
    private String address;
    private String detailAddress;
    private String zipcode;
    private boolean consentReceiveMarketing;
    private Gender gender;
    private String phoneNumber;
    private Grade grade;
    private boolean active;
    private String userNotice;
}
