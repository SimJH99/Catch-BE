package com.encore.thecatch.Post.dto.Request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdatePostReq {
    private String title;

    private String category;

    private String contents;

    private List<MultipartFile> images;
}
