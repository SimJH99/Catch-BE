package com.encore.thecatch.complaint.dto.response;

import com.encore.thecatch.complaint.entity.Status;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
public class MyComplaintRes {
    private Long id;
    private String title;
    private LocalDateTime createdTime;
    private Status status;

    @QueryProjection
    public MyComplaintRes(Long id, String title, LocalDateTime createdTime, Status status) {
        this.id = id;
        this.title = title;
        this.createdTime = createdTime;
        this.status = status;
    }
}
