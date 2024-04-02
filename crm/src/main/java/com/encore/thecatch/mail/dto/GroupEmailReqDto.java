package com.encore.thecatch.mail.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupEmailReqDto {
    private List<String> emailList;
    private String title;
    private String contents;
}
