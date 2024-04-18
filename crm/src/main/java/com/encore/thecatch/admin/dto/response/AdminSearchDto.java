package com.encore.thecatch.admin.dto.response;

import com.encore.thecatch.common.dto.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSearchDto {
    private Long id;
    private String name;
    private String employeeNumber;
    private String email;
    private Role role;
}
