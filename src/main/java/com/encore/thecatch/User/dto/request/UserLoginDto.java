package com.encore.thecatch.User.dto.request;

import lombok.Data;

@Data
public class UserLoginDto {
    private String email;
    private String password;
}
