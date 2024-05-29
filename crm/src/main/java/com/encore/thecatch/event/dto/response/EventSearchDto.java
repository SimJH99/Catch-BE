package com.encore.thecatch.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSearchDto {
    private Long id;
    private String name;
    private String startDate;
    private String endDate;
    private String eventStatus;
}
