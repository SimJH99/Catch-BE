package com.encore.thecatch.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
public class SignUpYear {
    private LocalDateTime day;
    private Long count;

    @QueryProjection
    public SignUpYear(LocalDateTime day, Long count) {
        this.day = day;
        this.count = count;
    }
}
