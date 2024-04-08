package com.encore.thecatch.complaint.dto.response;

import com.encore.thecatch.complaint.entity.Complaint;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DetailComplaintRes {
    private String title;

    private String category;

    private String contents;

    private List<String> s3Urls;

    public static DetailComplaintRes from(Complaint complaint, List<String> s3Urls){
        return DetailComplaintRes.builder()
                .title(complaint.getTitle())
                .category(complaint.getCategory())
                .contents(complaint.getContents())
                .s3Urls(s3Urls)
                .build() ;
    }

}
