package com.encore.thecatch.User.dto.response;

import com.encore.thecatch.User.domain.BirthDate;
import com.encore.thecatch.User.domain.TotalAddress;
import com.encore.thecatch.User.domain.User;
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
