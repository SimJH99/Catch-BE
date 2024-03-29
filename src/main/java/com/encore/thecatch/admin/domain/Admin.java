package com.encore.thecatch.admin.domain;

import com.encore.thecatch.common.dto.Role;
import com.encore.thecatch.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Admin extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String employeeNumber;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private Long companyId;

    @Enumerated(EnumType.STRING)
    private Role role;

}
