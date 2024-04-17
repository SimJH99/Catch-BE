package com.encore.thecatch.admin.dto.response;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.admin.dto.request.AdminSignUpDto;
import com.encore.thecatch.common.dto.Role;
import com.encore.thecatch.common.util.AesUtil;
import com.encore.thecatch.company.domain.Company;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class AdminInfoResDto {
    private String name;
    private String employeeNumber;
    private String email;
    private Role role;

    public static AdminInfoResDto toDto(AdminInfoDto adminInfoDto) {
        return AdminInfoResDto.builder()
                .name(adminInfoDto.getName())
                .employeeNumber(adminInfoDto.getEmployeeNumber())
                .email(adminInfoDto.getEmail())
                .role(adminInfoDto.getRole())
                .build();
    }
}
