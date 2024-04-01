package com.encore.thecatch.post.dto.response;

import com.encore.thecatch.post.entity.Post;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePostRes {
    private String title;

    private String category;

    private String contents;

    public static UpdatePostRes from (Post post){
        return UpdatePostRes.builder()
                .title(post.getTitle())
                .category(post.getCategory())
                .contents(post.getContents())
                .build();
    }
}
