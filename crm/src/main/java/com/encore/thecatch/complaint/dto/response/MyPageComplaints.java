package com.encore.thecatch.complaint.dto.response;

import com.encore.thecatch.complaint.entity.Status;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class MyPageComplaints {
    Long id;
    String title;
    Status status;

    @QueryProjection
    public MyPageComplaints(Long id, String title, Status status) {
        this.id = id;
        this.title = title;
        this.status = status;
    }
}
