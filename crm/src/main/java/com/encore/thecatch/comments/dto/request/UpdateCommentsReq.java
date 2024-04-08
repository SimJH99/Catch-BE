package com.encore.thecatch.comments.dto.request;

import lombok.Data;

import javax.persistence.Column;

@Data
public class UpdateCommentsReq {
    @Column(length = 500)
    private String comment;
}
