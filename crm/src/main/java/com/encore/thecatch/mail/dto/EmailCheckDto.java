package com.encore.thecatch.mail.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class EmailCheckDto {
    @NotEmpty(message = "이메일을 입력해 주세요")
    private String email;

    @NotEmpty(message = "인증 번호를 입력해 주세요")
    private String authNum;
}
