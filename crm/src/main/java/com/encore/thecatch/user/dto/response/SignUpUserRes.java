package com.encore.thecatch.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpUserRes {
    private Long dayUser;
    private Long lastDayUser;
    private Long weekUser;
    private Long lastWeekUser;
    private Long monthUser;
    private Long lastMonthUser;
}
