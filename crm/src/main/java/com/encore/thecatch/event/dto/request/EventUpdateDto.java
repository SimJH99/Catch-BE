package com.encore.thecatch.event.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EventUpdateDto {
    private String name;
    private String contents;
    private LocalDate startDate;
    private LocalDate endDate;
}
