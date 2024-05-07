package com.encore.thecatch.admin.dto.response;

import com.encore.thecatch.common.dto.Role;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDetailDto {
    private String name;
    private String employeeNumber;
    private String email;
    private Role role;
    private boolean active;
}
