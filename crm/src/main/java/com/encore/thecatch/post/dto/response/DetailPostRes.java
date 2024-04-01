package com.encore.thecatch.post.dto.response;

import com.encore.thecatch.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DetailPostRes {
    private String title;

    private String category;

    private String contents;

    private List<String> s3Urls;

    public static DetailPostRes from(Post post, List<String> s3Urls){
        return DetailPostRes.builder()
                .title(post.getTitle())
                .category(post.getCategory())
                .contents(post.getContents())
                .s3Urls(s3Urls)
                .build() ;
    }

}
