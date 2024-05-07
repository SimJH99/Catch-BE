package com.encore.thecatch.complaint.dto.response;

import com.encore.thecatch.complaint.entity.Status;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class CountStatusComplaintRes {
    private Status status;
    private Long count;

    @QueryProjection
    public CountStatusComplaintRes (Status status, Long count) {
        this.status = status;
        this.count = count;
    }
}
