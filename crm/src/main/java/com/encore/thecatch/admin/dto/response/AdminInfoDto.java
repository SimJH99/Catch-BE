package com.encore.thecatch.admin.dto.response;

import com.encore.thecatch.common.dto.Role;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class AdminInfoDto {
    private Long id;
    private String name;
    private String employeeNumber;
    private String email;
    private Role role;

    @QueryProjection
    public AdminInfoDto(
            Long id,
            String name,
            String employeeNumber,
            String email,
            Role role) {
        this.id = id;
        this.name = name;
        this.employeeNumber = employeeNumber;
        this.email = email;
        this.role = role;
    }
}
