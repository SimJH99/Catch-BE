package com.encore.thecatch.complaint.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
public class CountMonthComplaint {
    private LocalDateTime day;
    private String category;
    private Long count;

    @QueryProjection
    public CountMonthComplaint(LocalDateTime day, String category, Long count) {
        this.day = day;
        this.category = category;
        this.count = count;
    }
}
