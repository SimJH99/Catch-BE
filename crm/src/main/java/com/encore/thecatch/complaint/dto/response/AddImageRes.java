package com.encore.thecatch.complaint.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddImageRes {
    private String image;

    public static AddImageRes from(String imageUrl){
        return  AddImageRes.builder()
                .image(imageUrl)
                .build();
    }
}
