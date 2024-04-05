package com.encore.thecatch.comments.dto.response;

import com.encore.thecatch.comments.entity.Comments;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DetailCommentRes {

    private String comment;

    public static DetailCommentRes from(Comments comments) {
        return DetailCommentRes.builder()
                .comment(comments.getComment())
                .build();
    }

}
