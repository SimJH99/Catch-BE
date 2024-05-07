package com.encore.thecatch.complaint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountMonthComplaintRes {
    private int day;
    private String category;
    private Long count;

    public static CountMonthComplaintRes toDto(final CountMonthComplaint countMonthComplaint) {
        return CountMonthComplaintRes.builder()
                .day(countMonthComplaint.getDay().getDayOfMonth())
                .category(countMonthComplaint.getCategory())
                .count(countMonthComplaint.getCount())
                .build();
    }
}
