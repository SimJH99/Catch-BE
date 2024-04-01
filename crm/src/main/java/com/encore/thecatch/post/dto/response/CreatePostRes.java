package com.encore.thecatch.post.dto.response;

import com.encore.thecatch.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePostRes {
    private String title;

    private String category;

    private String contents;

    private Long userId;

    public static CreatePostRes from(Post post){
        return CreatePostRes.builder()
                .title(post.getTitle())
                .category(post.getCategory())
                .contents(post.getContents())
                .userId(post.getUser().getId())
                .build();
    }

}
