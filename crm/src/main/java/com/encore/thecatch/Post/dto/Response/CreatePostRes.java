package com.encore.thecatch.Post.dto.Response;

import com.encore.thecatch.Post.Entity.Post;
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
