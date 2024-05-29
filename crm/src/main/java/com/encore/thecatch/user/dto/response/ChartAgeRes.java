package com.encore.thecatch.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
public class ChartAgeRes {
    private String ageGroup;
    private Long count;

    @QueryProjection
    public ChartAgeRes(String ageGroup, Long count) {
        this.ageGroup = ageGroup;
        this.count = count;
    }
}
