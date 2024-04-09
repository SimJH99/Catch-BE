package com.encore.thecatch.user.dto.response;

import com.encore.thecatch.user.domain.Grade;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChartGradeRes {
    Grade grade;
    Long count;

    @QueryProjection
    public ChartGradeRes(Grade grade, Long count) {
        this.grade = grade;
        this.count = count;
    }
}
