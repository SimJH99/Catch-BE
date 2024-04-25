package com.encore.thecatch.event.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
public class EventDetailDto {
    private String name;
    private String contents;
    private LocalDate startDate;
    private LocalDate endDate;
}
