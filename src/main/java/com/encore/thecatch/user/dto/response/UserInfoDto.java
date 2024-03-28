package com.encore.thecatch.user.dto.response;

import com.encore.thecatch.user.domain.BirthDate;
import com.encore.thecatch.user.domain.TotalAddress;
import com.encore.thecatch.user.domain.User;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserInfoDto {
    private String name;
    private String email;
    private int year;
    private int month;
    private int day;
    private String address;
    private String detailAddress;
    private int zipcode;
    private boolean consentReceiveMarketing;

    public static UserInfoDto toUserInfoDto(User user) {
        UserInfoDtoBuilder builder = UserInfoDto.builder();
        builder.name(user.getName());
        builder.email(user.getEmail());
        BirthDate birthDate = user.getBirthDate();
        if(birthDate != null){
            builder.year(birthDate.getYear());
            builder.month(birthDate.getMonth());
            builder.day(birthDate.getDay());
        }
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
