package com.encore.thecatch.User.dto.request;

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
    private boolean consentReceiveMarketing;
}
