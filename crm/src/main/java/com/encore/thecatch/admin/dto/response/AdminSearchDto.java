package com.encore.thecatch.admin.dto.response;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.common.dto.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminSearchDto {
    private String name;
    private String employeeNumber;
    private String email;
    private Role role;

    public static AdminSearchDto toDto(Admin admin) {
        return AdminSearchDto.builder()
                .name(admin.getName())
                .email(admin.getEmail())
                .employeeNumber(admin.getEmployeeNumber())
                .build();
    }
}
