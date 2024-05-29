package com.encore.thecatch.comments.dto.response;

import com.encore.thecatch.comments.entity.Comments;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateCommentsRes {
    private String comment;

    public static UpdateCommentsRes from(Comments comments) {
        return UpdateCommentsRes.builder()
                .comment(comments.getComment())
                .build();
    }
}
