package com.encore.thecatch.admin.dto.request;

import com.encore.thecatch.common.dto.Role;
import lombok.Builder;
import lombok.Data;

@Data
public class AdminUpdateDto {
    private String name;
    private String email;
    private Role role;
}
