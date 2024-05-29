package com.encore.thecatch.complaint.dto.response;

import com.encore.thecatch.complaint.entity.Complaint;
import com.encore.thecatch.complaint.entity.Status;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class DetailComplaintRes {
    private String title;

    private String category;

    private String contents;

    private Status status;

    private Map<Long, String> s3Urls;

    public static DetailComplaintRes from(Complaint complaint, Map<Long, String> s3Urls) {
        return DetailComplaintRes.builder()
                .title(complaint.getTitle())
                .category(complaint.getCategory())
                .contents(complaint.getContents())
                .status(complaint.getStatus())
                .s3Urls(s3Urls)
                .build();
    }

}
