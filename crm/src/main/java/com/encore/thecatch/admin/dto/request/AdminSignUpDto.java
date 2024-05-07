package com.encore.thecatch.admin.dto.request;

import com.encore.thecatch.common.dto.Role;
import lombok.Data;


@Data
public class AdminSignUpDto {
    private String name;
    private String employeeNumber;
    private String email;
    private String password;
    private Long companyId;
    private Role role;
}
