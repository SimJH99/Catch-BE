package com.encore.thecatch.user.dto.response;

import com.encore.thecatch.user.domain.Gender;
import com.encore.thecatch.user.domain.Grade;
import com.encore.thecatch.user.domain.TotalAddress;
import com.encore.thecatch.user.domain.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Data
@Builder
public class UserInfoDto {


    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean active;
    private LocalDate birthDate;
    private String address;
    private String detailAddress;
    private String zipcode;
    private Grade grade;
    private Gender gender;
    private boolean consentReceiveMarketing;

    public static UserInfoDto toUserInfoDto(User user) {
        UserInfoDtoBuilder builder = UserInfoDto.builder();
        builder.id(user.getId());
        builder.name(user.getName());
        builder.email(user.getEmail());
        builder.phoneNumber(user.getPhoneNumber());
        builder.active(user.isActive());
        builder.birthDate(user.getBirthDate());
        TotalAddress totalAddress = user.getTotalAddress();
        if (totalAddress != null) {
            builder.address(totalAddress.getAddress());
            builder.detailAddress(totalAddress.getDetailAddress());
            builder.zipcode(totalAddress.getZipcode());
        }
        builder.consentReceiveMarketing(user.isConsentReceiveMarketing());
        builder.gender(user.getGender());
        builder.grade(user.getGrade());

        return builder.build();
    }
}
