package com.encore.thecatch.log.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class VisitTodayUserRes {
    private String email;

    @QueryProjection
    public VisitTodayUserRes(String email) {
        this.email = email;
    }
}
