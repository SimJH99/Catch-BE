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
    String title;
    Status status;

    @QueryProjection
    public MyPageComplaints(String title, Status status) {
        this.title = title;
        this.status = status;
    }
}
