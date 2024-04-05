package com.encore.thecatch.complaint.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddImageReq {
    private MultipartFile image;
}
