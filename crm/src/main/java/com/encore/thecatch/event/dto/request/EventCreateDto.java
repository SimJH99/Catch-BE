package com.encore.thecatch.event.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class
EventCreateDto {
    private String name;
    private String contents;
    private String startDate;
    private String endDate;
}
