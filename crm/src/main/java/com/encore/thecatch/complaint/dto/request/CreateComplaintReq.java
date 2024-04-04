package com.encore.thecatch.complaint.dto.request;

import com.encore.thecatch.complaint.entity.Complaint;
import com.encore.thecatch.user.domain.User;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateComplaintReq {

    private String title;

    private String category;

    private String contents;

    private List<MultipartFile> images;

    public Complaint toEntity(User user){
        return Complaint.builder()
                .title(title)
                .category(category)
                .user(user)
                .contents(contents)
                .build();
    }
}
