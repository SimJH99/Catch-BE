package com.encore.thecatch.admin.dto.response;

import com.encore.thecatch.common.dto.Role;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class AdminInfoDto {
    private String name;
    private String employeeNumber;
    private String email;
    private Role role;

    @QueryProjection
    public AdminInfoDto(
            String name,
            String employeeNumber,
            String email,
            Role role) {
        this.name = name;
        this.employeeNumber = employeeNumber;
        this.email = email;
        this.role = role;
    }
}
