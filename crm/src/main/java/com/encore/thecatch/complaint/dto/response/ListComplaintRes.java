package com.encore.thecatch.complaint.dto.response;

import com.encore.thecatch.complaint.entity.Status;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListComplaintRes {

    private Long complaintId;
    private String name;
    private String title;
    private Status status;


    @QueryProjection
    public ListComplaintRes (Long complaintId, String name, String title, Status status) {
        this.complaintId = complaintId;
        this.name = name;
        this.title = title;
        this.status = status;
    }
}
