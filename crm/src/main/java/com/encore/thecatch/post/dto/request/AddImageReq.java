package com.encore.thecatch.post.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddImageReq {

    private MultipartFile image;

}
