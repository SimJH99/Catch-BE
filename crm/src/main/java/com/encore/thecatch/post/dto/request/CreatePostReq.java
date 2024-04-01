package com.encore.thecatch.post.dto.request;

import com.encore.thecatch.post.entity.Post;
import com.encore.thecatch.user.domain.User;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreatePostReq {

    private String title;

    private String category;

    private String contents;

    private List<MultipartFile> images;

    public Post toEntity(List<String> imagePath, User user){
        return Post.builder()
                .title(title)
                .category(category)
                .user(user)
                .contents(contents)
                .build();
    }
}
