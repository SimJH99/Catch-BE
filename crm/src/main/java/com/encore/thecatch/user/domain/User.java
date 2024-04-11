package com.encore.thecatch.user.domain;

import com.encore.thecatch.common.dto.Role;
import com.encore.thecatch.common.entity.BaseEntity;
import com.encore.thecatch.company.domain.Company;
import com.encore.thecatch.user.dto.request.UserSignUpDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private LocalDate brithDate;
    @Column(nullable = false)
    private TotalAddress totalAddress; // (주소, 상세주소, 우편번호)
    @Column(nullable = false)
    private boolean consentReceiveMarketing; // 마케팅 수신 동의 여부 (true, false)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private String phoneNumber; // 전화번호

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    private boolean active;

    private String userNotice;

    public void userActiveToDisable() {
        this.active = false;
    }

    // 비밀번호 암호화 메서드
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void dataEncode(String name, String email, String phoneNumber, TotalAddress totalAddress) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.totalAddress = totalAddress;
    }

    public void dataDecode(String name, String email, String phoneNumber, TotalAddress totalAddress) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.totalAddress = totalAddress;
    }

    public static User toEntity(UserSignUpDto userSignUpDto, Company company) {
        TotalAddress totalAddress = TotalAddress.builder()
                .address(userSignUpDto.getAddress())
                .detailAddress(userSignUpDto.getDetailAddress())
                .zipcode(userSignUpDto.getZipcode())
                .build();

        User user = User.builder()
                .name(userSignUpDto.getName())
                .email(userSignUpDto.getEmail())
                .password(userSignUpDto.getPassword())
                .brithDate(userSignUpDto.getBirthDate())
                .totalAddress(totalAddress)
                .phoneNumber(userSignUpDto.getPhoneNumber())
                .role(Role.USER)
                .grade(Grade.SLIVER)
                .gender(userSignUpDto.getGender())
                .active(true)
                .consentReceiveMarketing(userSignUpDto.isConsentReceiveMarketing())
                .company(company)
                .build();

        return user;
    }
}