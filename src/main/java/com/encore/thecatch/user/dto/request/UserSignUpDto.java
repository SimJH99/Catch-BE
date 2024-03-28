package com.encore.thecatch.user.dto.request;

import com.encore.thecatch.common.dto.Role;
import lombok.Data;

@Data
public class UserSignUpDto {
    private String name;
    private String email;
    private String password;
    private int year;
    private int month;
    private int day;
    private String address;
    private String detailAddress;
    private int zipcode;
    private String phoneNumber;
    private Role role;
    private boolean consentReceiveMarketing;
}
