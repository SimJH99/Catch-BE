package com.encore.thecatch.admin.domain;

import com.encore.thecatch.admin.dto.request.AdminSignUpDto;
import com.encore.thecatch.common.dto.Role;
import com.encore.thecatch.common.entity.BaseEntity;
import com.encore.thecatch.company.domain.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Admin extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String employeeNumber;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    private boolean active;

    @Enumerated(EnumType.STRING)
    private Role role;
    public void passwordEncoder(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public static Admin toEntity(AdminSignUpDto adminSignUpDto, Company company) {
        return Admin.builder()
                .name(adminSignUpDto.getName())
                .employeeNumber(adminSignUpDto.getEmployeeNumber())
                .password(adminSignUpDto.getPassword())
                .email(adminSignUpDto.getEmail())
                .role(adminSignUpDto.getRole())
                .company(company)
                .active(true)
                .build();
    }

    public void dataEncode(String name, String employeeNumber, String email) {
        this.name = name;
        this.employeeNumber = employeeNumber;
        this.email = email;
    }

    public void dataDecode(String name, String employeeNumber, String email) {
        this.name = name;
        this.employeeNumber = employeeNumber;
        this.email = email;
    }

    public void masking(String maskingName, String maskingEmployeeNumber, String maskingEmail) {
        this.name = maskingName;
        this.employeeNumber = maskingEmployeeNumber;
        this.email = maskingEmail;
    }

    public void adminUpdate(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public void adminActiveToDisable() {
        this.active = false;
    }
    public void adminActiveToActivation(){
        this.active = true;
    }

}
