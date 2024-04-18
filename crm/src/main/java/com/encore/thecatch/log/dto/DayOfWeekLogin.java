package com.encore.thecatch.log.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DayOfWeekLogin {
    private String day;
    private Long count;

    @QueryProjection
    public DayOfWeekLogin (String day, Long count) {
        this.day = day;
        this.count = count;
    }
}
