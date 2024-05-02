package com.encore.thecatch.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpYearRes {
    private int month;
    private Long count;

    public static SignUpYearRes toDto(final SignUpYear signUpYear) {
        return SignUpYearRes.builder()
                .month(signUpYear.getDay().getMonthValue())
                .count(signUpYear.getCount())
                .build();
    }
}
