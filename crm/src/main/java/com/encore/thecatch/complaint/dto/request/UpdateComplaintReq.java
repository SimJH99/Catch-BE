package com.encore.thecatch.complaint.dto.request;

import lombok.Data;

@Data
public class UpdateComplaintReq {
    private String title;

    private String category;

    private String contents;
}
