package com.encore.thecatch.mail.dto;

import com.encore.thecatch.comments.entity.Comments;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CommentsEmailDto {
    String userName;
    String userEmail;
    String complaintContents;
    String adminCompany;
    String commentContents;
    LocalDate ComplaintCreatedTime;
}
