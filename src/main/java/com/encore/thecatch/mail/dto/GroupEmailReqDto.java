package com.encore.thecatch.mail.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class GroupEmailReqDto {
    private List<String> emailList;
    private String title;
    private String contents;
}
