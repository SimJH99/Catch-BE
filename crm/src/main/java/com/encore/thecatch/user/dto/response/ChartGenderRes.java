package com.encore.thecatch.user.dto.response;

import com.encore.thecatch.user.domain.Gender;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChartGenderRes {
    private Gender gender;
    private Long count;

    @QueryProjection
    public ChartGenderRes(Gender gender, Long count) {
        this.gender = gender;
        this.count = count;
    }

}
