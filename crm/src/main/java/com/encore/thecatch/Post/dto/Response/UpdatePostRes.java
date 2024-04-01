package com.encore.thecatch.Post.dto.Response;

import com.encore.thecatch.Post.Entity.Image;
import com.encore.thecatch.Post.Entity.Post;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdatePostRes {
    private String title;

    private String category;

    private String contents;

    private List<Image> imagePath;

    public static UpdatePostRes from (Post post){
        return UpdatePostRes.builder()
                .title(post.getTitle())
                .category(post.getCategory())
                .contents(post.getContents())
                .imagePath(post.getImgList())
                .build();
    }
}
