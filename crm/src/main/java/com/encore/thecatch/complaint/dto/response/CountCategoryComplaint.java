package com.encore.thecatch.complaint.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class CountCategoryComplaint {
    private String category;
    private Long count;

    @QueryProjection
    public CountCategoryComplaint (String category, Long count) {
        this.category = category;
        this.count = count;
    }
}
