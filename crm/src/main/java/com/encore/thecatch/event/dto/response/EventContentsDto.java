package com.encore.thecatch.event.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventContentsDto {
    private String contents;
}
