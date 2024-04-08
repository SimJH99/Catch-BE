package com.encore.thecatch.user.dto.response;

import com.encore.thecatch.user.domain.TotalAddress;
import com.encore.thecatch.user.domain.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Data
@Builder
public class UserInfoDto {
    private String name;
    private String email;
    private LocalDate birthDate;
    private String address;
    private String detailAddress;
    private String zipcode;
    private boolean consentReceiveMarketing;

    public static UserInfoDto toUserInfoDto(User user) {
        UserInfoDtoBuilder builder = UserInfoDto.builder();
        builder.name(user.getName());
        builder.email(user.getEmail());
        builder.birthDate(user.getBrithDate());
        TotalAddress totalAddress = user.getTotalAddress();
        if (totalAddress != null) {
            builder.address(totalAddress.getAddress());
            builder.detailAddress(totalAddress.getDetailAddress());
            builder.zipcode(totalAddress.getZipcode());
        }
        builder.consentReceiveMarketing(user.isConsentReceiveMarketing());

        return builder.build();
    }
}
