package com.encore.thecatch.complaint.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CountStatusComplaintRes {
    private Long count;

    @QueryProjection
    public CountStatusComplaintRes(Long count) {
        this.count = count;
    }
}
