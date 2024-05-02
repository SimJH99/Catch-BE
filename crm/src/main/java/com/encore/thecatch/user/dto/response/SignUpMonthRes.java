package com.encore.thecatch.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpMonthRes {
    private int day;
    private Long count;

    public static SignUpMonthRes toDto(final SignUpMonth signUpMonth) {
        return SignUpMonthRes.builder()
                .day(signUpMonth.getDay().getDayOfMonth())
                .count(signUpMonth.getCount())
                .build();
    }
}
