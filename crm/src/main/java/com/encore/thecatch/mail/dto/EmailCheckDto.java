package com.encore.thecatch.mail.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class EmailCheckDto {
    private String employeeNumber;
    private String authNumber;
}
