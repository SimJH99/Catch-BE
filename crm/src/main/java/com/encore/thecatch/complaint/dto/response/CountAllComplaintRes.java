package com.encore.thecatch.complaint.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CountAllComplaintRes {
    private Long count;

    @QueryProjection
    public CountAllComplaintRes (Long count) {
        this.count = count;
    }
}
