package com.encore.thecatch.admin.dto.response;

import com.encore.thecatch.common.dto.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminProfileDto {
    private String name;
    private String employeeNumber;
    private Role role;
}
