package com.encore.thecatch.admin.domain;

import com.encore.thecatch.admin.dto.request.AdminSignUpDto;
import com.encore.thecatch.common.dto.Role;
import com.encore.thecatch.common.entity.BaseEntity;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.user.domain.TotalAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

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
                .role(adminSignUpDto.getRole())
                .company(company)
                .build();
    }

    public void dataEncode(String name, String employeeNumber) {
        this.name = name;
        this.employeeNumber = employeeNumber;
    }

    public void dataDecode(String name, String employeeNumber) {
        this.name = name;
        this.employeeNumber = employeeNumber;
    }
}
