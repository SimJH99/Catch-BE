package com.encore.thecatch.complaint.dto.request;

import com.encore.thecatch.complaint.entity.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchComplaintCondition {
    private Long complaintId;
    private String name;
    private String title;
    private Status status;
    private int pageNo;
    private String category;
}
