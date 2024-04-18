package com.encore.thecatch.comments.dto.request;

import com.encore.thecatch.admin.domain.Admin;
import com.encore.thecatch.comments.entity.Comments;
import com.encore.thecatch.complaint.entity.Complaint;
import lombok.Data;

import javax.persistence.Column;

@Data
public class CreateCommentsReq {
    @Column(length = 500)
    private String comment;
    public Comments toEntity(Complaint complaint, Admin admin) {
        return Comments.builder()
                .complaint(complaint)

                .admin(admin)
                .comment(comment)
                .build();
    }
}
