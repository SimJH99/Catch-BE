package com.encore.thecatch.comments.dto.response;

import com.encore.thecatch.comments.entity.Comments;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DetailCommentRes {

    private String adminEmployeeNumber;
    private String comment;


    public static DetailCommentRes from(Comments comments) {
        return DetailCommentRes.builder()
                .adminEmployeeNumber(comments.getAdmin().getEmployeeNumber())
                .comment(comments.getComment())
                .build();
    }

}
