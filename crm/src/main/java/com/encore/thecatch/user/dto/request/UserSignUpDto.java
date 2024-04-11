package com.encore.thecatch.user.dto.request;

import com.encore.thecatch.common.dto.Role;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.user.domain.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserSignUpDto {
    private String name;
    private String email;
    private String password;
    private LocalDate birthDate;
    private String address;
    private String detailAddress;
    private String zipcode;
    private String phoneNumber;
    private Role role;
    private boolean consentReceiveMarketing;
    private Long companyId;
    private Gender gender;

}