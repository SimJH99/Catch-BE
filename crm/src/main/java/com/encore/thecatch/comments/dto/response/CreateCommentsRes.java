package com.encore.thecatch.comments.dto.response;


import com.encore.thecatch.comments.entity.Comments;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateCommentsRes {
    private Long id;
    private String comment;

    public static CreateCommentsRes from(Comments comments) {
        return CreateCommentsRes.builder()
                .comment(comments.getComment())
                .id(comments.getId())
                .build();
    }
}

