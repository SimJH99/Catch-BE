package com.encore.thecatch.complaint.dto.response;

import com.encore.thecatch.complaint.entity.Complaint;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateComplaintRes {
    private String title;

    private String category;

    private String contents;

    public static UpdateComplaintRes from (Complaint complaint){
        return UpdateComplaintRes.builder()
                .title(complaint.getTitle())
                .category(complaint.getCategory())
                .contents(complaint.getContents())
                .build();
    }
}
