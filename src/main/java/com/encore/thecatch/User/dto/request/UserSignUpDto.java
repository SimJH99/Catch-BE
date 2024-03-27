package com.encore.thecatch.User.dto.request;

import com.encore.thecatch.User.domain.Role;
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
