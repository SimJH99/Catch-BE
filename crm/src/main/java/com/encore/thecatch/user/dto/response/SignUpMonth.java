package com.encore.thecatch.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class SignUpMonth {
    private LocalDateTime day;
    private Long count;

    @QueryProjection
    public SignUpMonth(LocalDateTime day, Long count) {
        this.day = day;
        this.count = count;
    }
}
