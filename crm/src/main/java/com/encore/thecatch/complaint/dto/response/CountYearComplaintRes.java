package com.encore.thecatch.complaint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountYearComplaintRes {
    private int day;
    private String category;
    private Long count;

    public static CountYearComplaintRes toDto(final CountYearComplaint countYearComplaint) {
        return CountYearComplaintRes.builder()
                .day(countYearComplaint.getDay().getMonthValue())
                .category(countYearComplaint.getCategory())
                .count(countYearComplaint.getCount())
                .build();
    }
}
