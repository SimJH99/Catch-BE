package com.encore.thecatch.admin.dto.request;

import lombok.Data;

@Data
public class AdminLoginDto {
    private String employeeNumber;
    private String password;
}
