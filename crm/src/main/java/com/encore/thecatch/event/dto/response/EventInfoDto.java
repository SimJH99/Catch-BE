package com.encore.thecatch.event.dto.response;

import com.encore.thecatch.event.domain.EventStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
public class EventInfoDto {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private EventStatus eventStatus;

    @QueryProjection
    public EventInfoDto(
            Long id,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            EventStatus eventStatus) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventStatus =eventStatus;
    }
}
